package com.example.school.controller;

import com.example.school.entity.AppSettings;
import com.example.school.entity.Establishment;
import com.example.school.entity.ModuleStatus;
import com.example.school.entity.Role;
import com.example.school.entity.SchoolType;
import com.example.school.entity.SupportTicket;
import com.example.school.entity.SupportTicketMessage;
import com.example.school.entity.SupportTicketTarget;
import com.example.school.repository.AppSettingsRepository;
import com.example.school.repository.EstablishmentRepository;
import com.example.school.repository.ModuleSubscriptionRepository;
import com.example.school.repository.SchoolTypeRepository;
import com.example.school.repository.RoleRepository;
import com.example.school.repository.SupportTicketMessageRepository;
import com.example.school.repository.SupportTicketRepository;
import com.example.school.repository.UserRepository;
import com.example.school.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private AppSettingsRepository appSettingsRepository;

    @Autowired
    private SchoolTypeRepository schoolTypeRepository;

    @Autowired
    private ModuleSubscriptionRepository moduleSubscriptionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private SupportTicketMessageRepository supportTicketMessageRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String landing() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Establishment> establishments = establishmentRepository.findAll();
        List<SchoolType> schoolTypes = schoolTypeRepository.findAll();
        List<com.example.school.entity.User> globalUsers = userRepository.findByEstablishmentIdIsNull();

        long totalEstablishments = establishments.size();
        long activeEstablishments = establishments.stream().filter(Establishment::isActive).count();
        long deployedEstablishments = establishments.stream().filter(Establishment::isDeployed).count();
        long pendingEstablishments = totalEstablishments - activeEstablishments;
        long connectedNow = userRepository.countByLastLoginAtAfterAndEnabledIsTrue(LocalDateTime.now().minusMinutes(30));
        
        model.addAttribute("establishments", establishments);
        model.addAttribute("schoolTypes", schoolTypes);
        model.addAttribute("newEstablishment", new Establishment());
        model.addAttribute("totalEstablishments", totalEstablishments);
        model.addAttribute("activeEstablishments", activeEstablishments);
        model.addAttribute("deployedEstablishments", deployedEstablishments);
        model.addAttribute("pendingEstablishments", pendingEstablishments);
        model.addAttribute("connectedNow", connectedNow);
        model.addAttribute("countriesList", establishments.stream().map(Establishment::getCountry).filter(c -> c != null && !c.isBlank()).distinct().sorted().toList());
        model.addAttribute("pendingModuleRequests", moduleSubscriptionRepository.findByStatus(ModuleStatus.REQUESTED));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = auth != null ? auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet()) : Set.of();
        boolean isSuperAdmin = roles.contains("ROLE_SUPER_ADMIN");
        boolean isDirection = roles.contains("ROLE_DIRECTION");
        boolean isAdhesion = roles.contains("ROLE_ADHESION");

        List<SupportTicket> supportTickets;
        if (isDirection || isSuperAdmin) {
            supportTickets = supportTicketRepository.findAllByOrderByUpdatedAtDesc();
        } else if (isAdhesion) {
            supportTickets = supportTicketRepository.findByTargetOrderByUpdatedAtDesc(SupportTicketTarget.ADHESION);
        } else {
            supportTickets = List.of();
        }
        model.addAttribute("supportTickets", supportTickets);
        if (!supportTickets.isEmpty()) {
            List<Long> ids = supportTickets.stream().map(SupportTicket::getId).toList();
            Map<Long, List<SupportTicketMessage>> messagesByTicketId = supportTicketMessageRepository
                    .findByTicket_IdInOrderByCreatedAtAsc(ids)
                    .stream()
                    .collect(Collectors.groupingBy(m -> m.getTicket().getId()));
            model.addAttribute("ticketMessagesById", messagesByTicketId);
        } else {
            model.addAttribute("ticketMessagesById", Map.of());
        }
        model.addAttribute(
                "backofficeUsers",
                globalUsers.stream()
                        .filter(u -> u.getRoles().stream().anyMatch(r -> Set.of("SUPER_ADMIN", "DIRECTION", "ADHESION").contains(r.getName())))
                        .toList()
        );
        model.addAttribute(
                "backofficeRoles",
                roleRepository.findAll().stream()
                        .filter(r -> Set.of("DIRECTION", "ADHESION", "SUPER_ADMIN").contains(r.getName()))
                        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                        .collect(Collectors.toList())
        );
        return "dashboard";
    }

    @PostMapping("/dashboard/create-user")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String createBackofficeUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Nom d'utilisateur et mot de passe requis.");
                return "redirect:/dashboard";
            }
            if (!Set.of("DIRECTION", "ADHESION", "SUPER_ADMIN").contains(role)) {
                redirectAttributes.addFlashAttribute("error", "Role invalide.");
                return "redirect:/dashboard";
            }
            if (userRepository.findByUsername(username.trim().toLowerCase()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur existe deja.");
                return "redirect:/dashboard";
            }

            Role r = roleRepository.findByName(role)
                    .orElseThrow(() -> new IllegalStateException("Role introuvable: " + role));

            com.example.school.entity.User u = new com.example.school.entity.User();
            u.setUsername(username.trim().toLowerCase());
            u.setPassword(passwordEncoder.encode(password));
            u.setEnabled(true);
            u.setEstablishmentId(null);
            u.getRoles().add(r);
            userRepository.save(u);

            boolean emailSent = false;
            if (u.getUsername().contains("@")) {
                String subject = "OSM - Acces backoffice (" + role + ")";
                String body = "Bonjour,\n\n" +
                        "Un compte backoffice OSM a ete cree.\n\n" +
                        "Role       : " + role + "\n" +
                        "Identifiant: " + u.getUsername() + "\n" +
                        "Mot de passe: " + password + "\n\n" +
                        "Connexion : http://localhost:8081/login\n\n" +
                        "OSM Open Suku Manager";
                emailSent = emailService.send(u.getUsername(), subject, body);
            }

            redirectAttributes.addFlashAttribute("success", emailSent ? "Utilisateur cree et email envoye." : "Utilisateur cree.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur creation utilisateur : " + ex.getMessage());
        }
        return "redirect:/dashboard?open=liste";
    }

    @PostMapping("/dashboard/create-establishment")
    public String createEstablishment(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String slug,
            @RequestParam String address,
            @RequestParam String directorCivility,
            @RequestParam String directorName,
            @RequestParam String directorPhone,
            @RequestParam String directorEmail,
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(required = false) String logoUrl,
            @RequestParam String cfe,
            @RequestParam Long typeId,
            RedirectAttributes redirectAttributes) {
        Long createdId = null;
        try {
            SchoolType schoolType = schoolTypeRepository.findById(typeId)
                    .orElseThrow(() -> new RuntimeException("School type not found"));

            // Doublon : CFE doit être unique
            String normalizedCfe = cfe != null ? cfe.trim() : "";
            boolean duplicateCfe = establishmentRepository.findByCfeIgnoreCase(normalizedCfe)
                    .stream()
                    .anyMatch(existing -> id == null || !existing.getId().equals(id));
            if (duplicateCfe) {
                redirectAttributes.addFlashAttribute("error", "Etablissement déjà existant (même numéro CFE).");
                return "redirect:/dashboard";
            }
            
            Establishment establishment;
            boolean isEdit = id != null;
            if (isEdit) {
                establishment = establishmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Establishment not found"));
            } else {
                establishment = new Establishment();
                establishment.setActive(false);
                establishment.setDeployed(false);
                establishment.setCreationDate(LocalDate.now());
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                establishment.setCreatedBy(auth != null ? auth.getName() : "SYSTEM");
            }

            establishment.setName(name);
            establishment.setAddress(address);
            establishment.setType(schoolType);
            establishment.setDirectorCivility(directorCivility);
            establishment.setDirectorName(directorName);
            establishment.setDirectorPhone(directorPhone);
            establishment.setDirectorEmail(directorEmail);
            establishment.setCountry(country);
            establishment.setCity(city);
            if (logoUrl != null && !logoUrl.isBlank()) {
                establishment.setLogoUrl(logoUrl);
            }
            establishment.setCfe(normalizedCfe);
            String baseSlug = slugify(slug != null && !slug.isBlank() ? slug : name);
            if (baseSlug.isEmpty()) {
                baseSlug = "etablissement";
            }
            establishment.setSlug(ensureUniqueSlug(baseSlug, establishment.getId()));
            
            establishmentRepository.save(establishment);
            createdId = establishment.getId();
            redirectAttributes.addFlashAttribute("success", isEdit ? "Etablissement modifie avec succes !" : "Etablissement cree avec succes !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/dashboard";
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/theme")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateTheme(
            @RequestParam String themePrimary,
            RedirectAttributes redirectAttributes) {
        try {
            String value = themePrimary != null ? themePrimary.trim() : "";
            if (!value.matches("^#[0-9A-Fa-f]{6}$")) {
                redirectAttributes.addFlashAttribute("error", "Couleur invalide.");
                return "redirect:/dashboard";
            }
            AppSettings settings = appSettingsRepository.findById(1L).orElseGet(() -> {
                AppSettings s = new AppSettings();
                s.setId(1L);
                return s;
            });
            settings.setThemePrimary(value);
            appSettingsRepository.save(settings);
            redirectAttributes.addFlashAttribute("success", "Couleur globale mise a jour.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur mise a jour couleur : " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/create-establishment")
    public String createEstablishmentGet() {
        return "redirect:/dashboard";
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
        StringBuilder sb = new StringBuilder();
        int len = 12;
        for (int i = 0; i < len; i++) {
            int idx = (int) Math.floor(Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private String slugify(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug;
    }

    private String ensureUniqueSlug(String base, Long currentId) {
        String candidate = base;
        int suffix = 2;
        while (true) {
            var existing = establishmentRepository.findBySlugIgnoreCase(candidate);
            if (existing.isEmpty() || (currentId != null && existing.get().getId().equals(currentId))) {
                return candidate;
            }
            candidate = base + "-" + suffix;
            suffix++;
        }
    }

    private boolean looksLikeEmail(String value) {
        if (value == null) return false;
        String v = value.trim();
        return v.contains("@") && v.contains(".");
    }

    @PostMapping("/dashboard/establishments/{id}/logo/delete")
    @PreAuthorize("hasRole('DIRECTION')")
    public ResponseEntity<Map<String, String>> deleteEstablishmentLogo(@PathVariable Long id) {
        return establishmentRepository.findById(id)
                .map(est -> {
                    est.setLogoUrl(null);
                    establishmentRepository.save(est);
                    return ResponseEntity.ok(Map.of("message", "Logo supprimé"));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
