package com.example.school.controller;

import com.example.school.entity.Establishment;
import com.example.school.entity.ModuleCategory;
import com.example.school.entity.ModuleDefinition;
import com.example.school.entity.ModuleStatus;
import com.example.school.entity.ModuleSubscription;
import com.example.school.entity.SubscriptionPlan;
import com.example.school.repository.EstablishmentRepository;
import com.example.school.repository.ModuleDefinitionRepository;
import com.example.school.repository.ModuleSubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/establishments/{establishmentId}/modules")
public class AdminEstablishmentModuleController {

    private final EstablishmentRepository establishmentRepository;
    private final ModuleDefinitionRepository moduleDefinitionRepository;
    private final ModuleSubscriptionRepository moduleSubscriptionRepository;

    public AdminEstablishmentModuleController(
            EstablishmentRepository establishmentRepository,
            ModuleDefinitionRepository moduleDefinitionRepository,
            ModuleSubscriptionRepository moduleSubscriptionRepository
    ) {
        this.establishmentRepository = establishmentRepository;
        this.moduleDefinitionRepository = moduleDefinitionRepository;
        this.moduleSubscriptionRepository = moduleSubscriptionRepository;
    }

    public record PremiumModuleItem(
            Long moduleId,
            String code,
            String name,
            String description,
            ModuleStatus status,
            boolean paid,
            LocalDateTime requestedAt,
            LocalDateTime activatedAt,
            String activatedBy
    ) {}

    public record PremiumModulesResponse(
            Long establishmentId,
            String establishmentName,
            SubscriptionPlan plan,
            List<PremiumModuleItem> modules
    ) {}

    @GetMapping("/premium")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listPremiumModules(@PathVariable Long establishmentId) {
        Optional<Establishment> establishment = establishmentRepository.findById(establishmentId);
        if (establishment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<Long, ModuleSubscription> subsByModuleId = moduleSubscriptionRepository.findByEstablishmentId(establishmentId)
                .stream()
                .collect(Collectors.toMap(s -> s.getModule().getId(), s -> s, (a, b) -> a));

        List<PremiumModuleItem> modules = moduleDefinitionRepository.findByCategory(ModuleCategory.PREMIUM)
                .stream()
                .sorted(Comparator.comparing(ModuleDefinition::getName, String.CASE_INSENSITIVE_ORDER))
                .map(m -> {
                    ModuleSubscription sub = subsByModuleId.get(m.getId());
                    ModuleStatus status = sub != null ? sub.getStatus() : ModuleStatus.INACTIVE;
                    return new PremiumModuleItem(
                            m.getId(),
                            m.getCode(),
                            m.getName(),
                            m.getDescription(),
                            status,
                            true,
                            sub != null ? sub.getRequestedAt() : null,
                            sub != null ? sub.getActivatedAt() : null,
                            sub != null ? sub.getActivatedBy() : null
                    );
                })
                .toList();

        Establishment e = establishment.get();
        return ResponseEntity.ok(new PremiumModulesResponse(e.getId(), e.getName(), e.getSubscriptionPlan(), modules));
    }

    @PostMapping("/premium/{moduleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> setPremiumModuleStatus(
            @PathVariable Long establishmentId,
            @PathVariable Long moduleId,
            @RequestParam String status
    ) {
        Optional<Establishment> establishmentOpt = establishmentRepository.findById(establishmentId);
        if (establishmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<ModuleDefinition> moduleOpt = moduleDefinitionRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Establishment establishment = establishmentOpt.get();
        ModuleDefinition module = moduleOpt.get();
        if (module.getCategory() != ModuleCategory.PREMIUM) {
            return ResponseEntity.badRequest().body("Only premium modules can be managed here.");
        }

        ModuleStatus target;
        try {
            target = ModuleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        }
        if (target != ModuleStatus.ACTIVE && target != ModuleStatus.INACTIVE) {
            return ResponseEntity.badRequest().body("Status must be ACTIVE or INACTIVE.");
        }
        if (target == ModuleStatus.ACTIVE && establishment.getSubscriptionPlan() != SubscriptionPlan.PREMIUM) {
            return ResponseEntity.badRequest().body("Plan BASIC: premium modules cannot be activated.");
        }

        ModuleSubscription sub = moduleSubscriptionRepository
                .findByEstablishmentIdAndModuleId(establishmentId, moduleId)
                .orElseGet(() -> new ModuleSubscription(establishment, module, ModuleStatus.INACTIVE));

        if (target == ModuleStatus.ACTIVE) {
            sub.setStatus(ModuleStatus.ACTIVE);
            sub.setActivatedAt(LocalDateTime.now());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            sub.setActivatedBy(auth != null ? auth.getName() : "SYSTEM");
        } else {
            sub.setStatus(ModuleStatus.INACTIVE);
            sub.setActivatedAt(null);
            sub.setActivatedBy(null);
        }
        moduleSubscriptionRepository.save(sub);
        return ResponseEntity.ok(sub);
    }
}
