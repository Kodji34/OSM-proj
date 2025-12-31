package com.example.school.controller;

import com.example.school.entity.ModuleStatus;
import com.example.school.entity.ModuleSubscription;
import com.example.school.repository.ModuleSubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/modules")
public class AdminModuleController {

    private final ModuleSubscriptionRepository moduleSubscriptionRepository;

    public AdminModuleController(ModuleSubscriptionRepository moduleSubscriptionRepository) {
        this.moduleSubscriptionRepository = moduleSubscriptionRepository;
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        ModuleSubscription sub = moduleSubscriptionRepository.findById( id).orElse(null);
        if (sub == null) {
            return ResponseEntity.notFound().build();
        }

        if (sub.getStatus() != ModuleStatus.ACTIVE) {
            sub.setStatus(ModuleStatus.ACTIVE);
            sub.setActivatedAt(LocalDateTime.now());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            sub.setActivatedBy(auth != null ? auth.getName() : "SYSTEM");
            moduleSubscriptionRepository.save(sub);
        }
        return ResponseEntity.ok(sub);
    }
}

