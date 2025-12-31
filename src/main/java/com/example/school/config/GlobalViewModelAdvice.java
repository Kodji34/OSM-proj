package com.example.school.config;

import com.example.school.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = Controller.class)
public class GlobalViewModelAdvice {

    private final UserRepository userRepository;

    public GlobalViewModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication authentication) {
        model.addAttribute("today", LocalDate.now());

        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            return;
        }

        String username = authentication.getName();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());

        model.addAttribute("currentUsername", username);
        model.addAttribute("currentRoles", roles);

        boolean isSuperAdmin = roles.contains("ROLE_SUPER_ADMIN");
        boolean isDirection = roles.contains("ROLE_DIRECTION");
        boolean isAdhesion = roles.contains("ROLE_ADHESION");
        boolean isTenantAdmin = username.startsWith("admin_est_");

        model.addAttribute("isSuperAdmin", isSuperAdmin);
        model.addAttribute("isDirection", isDirection);
        model.addAttribute("isAdhesion", isAdhesion);
        model.addAttribute("isTenantAdmin", isTenantAdmin);

        String label;
        if (isSuperAdmin) {
            label = "Super administrateur";
        } else if (isDirection) {
            label = "Direction";
        } else if (isAdhesion) {
            label = "Responsable des adhesions";
        } else if (isTenantAdmin) {
            label = "Admin etablissement";
        } else if (roles.contains("ROLE_ADMIN")) {
            label = "Administrateur";
        } else if (roles.contains("ROLE_USER")) {
            label = "Utilisateur";
        } else {
            label = "Utilisateur";
        }
        model.addAttribute("currentRoleLabel", label);

        userRepository.findByUsername(username).ifPresent(u -> {
            model.addAttribute("lastLoginAt", u.getLastLoginAt());
            model.addAttribute("previousLoginAt", u.getPreviousLoginAt());
        });
    }
}

