package com.example.school.controller;

import com.example.school.entity.SupportTicket;
import com.example.school.entity.SupportTicketCategory;
import com.example.school.entity.SupportTicketMessage;
import com.example.school.entity.SupportTicketStatus;
import com.example.school.repository.SupportTicketMessageRepository;
import com.example.school.repository.SupportTicketRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BackofficeSupportController {

    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMessageRepository messageRepository;

    public BackofficeSupportController(SupportTicketRepository ticketRepository, SupportTicketMessageRepository messageRepository) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/dashboard/tickets/{ticketId}/message")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DIRECTION','ADHESION')")
    public String reply(
            @PathVariable Long ticketId,
            @RequestParam String message,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (message == null || message.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Message requis.");
                return "redirect:/dashboard";
            }

            SupportTicket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable"));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null && auth.getName() != null ? auth.getName() : "SYSTEM";
            String roleLabel = resolveRoleLabel(auth, username);

            messageRepository.save(new SupportTicketMessage(ticket, username, roleLabel, message.trim()));
            ticket.setUpdatedBy(username);
            ticketRepository.save(ticket);

            redirectAttributes.addFlashAttribute("success", "Reponse envoyee.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur reponse : " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/tickets/{ticketId}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADHESION')")
    public String updateStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            SupportTicket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable"));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null && auth.getName() != null ? auth.getName() : "SYSTEM";
            boolean isSuperAdmin = hasRole(auth, "ROLE_SUPER_ADMIN");
            boolean isAdhesion = hasRole(auth, "ROLE_ADHESION");

            SupportTicketStatus next = SupportTicketStatus.valueOf(status.trim().toUpperCase());

            if (isAdhesion && !isSuperAdmin) {
                if (ticket.getCategory() != SupportTicketCategory.FEATURE) {
                    throw new IllegalStateException("Seules les demandes de fonctionnalites sont gerables par l'adhesion.");
                }
                if (next != SupportTicketStatus.APPROVED && next != SupportTicketStatus.REJECTED) {
                    throw new IllegalStateException("Statut invalide (adhesion).");
                }
            }

            if (isSuperAdmin && !isAdhesion) {
                if (next == SupportTicketStatus.APPROVED || next == SupportTicketStatus.REJECTED) {
                    throw new IllegalStateException("Ce statut est reserve a l'adhesion.");
                }
            }

            ticket.setStatus(next);
            ticket.setUpdatedBy(username);
            ticketRepository.save(ticket);

            redirectAttributes.addFlashAttribute("success", "Statut mis a jour.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", "Statut invalide.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur statut : " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream().anyMatch(a -> role.equals(a.getAuthority()));
    }

    private String resolveRoleLabel(Authentication authentication, String username) {
        if (authentication == null) return "Utilisateur";
        Set<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
        if (roles.contains("ROLE_SUPER_ADMIN")) return "Super administrateur";
        if (roles.contains("ROLE_DIRECTION")) return "Direction";
        if (roles.contains("ROLE_ADHESION")) return "Responsable des adhesions";
        if (username != null && username.startsWith("admin_est_")) return "Admin etablissement";
        if (roles.contains("ROLE_ADMIN")) return "Administrateur";
        if (roles.contains("ROLE_USER")) return "Utilisateur";
        return "Utilisateur";
    }
}

