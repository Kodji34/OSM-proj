package com.example.school.controller;

import com.example.school.entity.Establishment;
import com.example.school.entity.SupportTicket;
import com.example.school.entity.SupportTicketCategory;
import com.example.school.entity.SupportTicketMessage;
import com.example.school.entity.SupportTicketTarget;
import com.example.school.entity.User;
import com.example.school.repository.EstablishmentRepository;
import com.example.school.repository.SupportTicketMessageRepository;
import com.example.school.repository.SupportTicketRepository;
import com.example.school.repository.UserRepository;
import com.example.school.service.EmailService;
import com.example.school.tenant.TenantContext;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TenantSupportController {

    private final EstablishmentRepository establishmentRepository;
    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public TenantSupportController(
            EstablishmentRepository establishmentRepository,
            SupportTicketRepository ticketRepository,
            SupportTicketMessageRepository messageRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.establishmentRepository = establishmentRepository;
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/tenant/dashboard/tickets")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public String createTicket(
            @RequestParam String category,
            @RequestParam String subject,
            @RequestParam String message,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Establishment> est = loadCurrentTenant();
        if (est.isEmpty()) return "error-tenant";

        try {
            if (subject == null || subject.isBlank() || message == null || message.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Sujet et message requis.");
                return "redirect:/tenant/dashboard";
            }

            SupportTicketCategory cat = SupportTicketCategory.valueOf(category.trim().toUpperCase());
            SupportTicketTarget target = SupportTicketTarget.SUPER_ADMIN;

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null && auth.getName() != null ? auth.getName() : "SYSTEM";
            String roleLabel = resolveRoleLabel(auth, username);

            SupportTicket ticket = new SupportTicket(est.get(), cat, target, subject.trim(), username);
            ticket = ticketRepository.save(ticket);

            SupportTicketMessage first = new SupportTicketMessage(ticket, username, roleLabel, message.trim());
            messageRepository.save(first);

            notifyTicketByEmail(est.get(), ticket, message.trim(), username);
            redirectAttributes.addFlashAttribute("success", "Message envoye. Un responsable vous repondra bientot.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", "Categorie invalide.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur envoi message : " + ex.getMessage());
        }
        return "redirect:/tenant/dashboard";
    }

    @PostMapping("/tenant/dashboard/tickets/{ticketId}/message")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public String replyTicket(
            @PathVariable Long ticketId,
            @RequestParam String message,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Establishment> est = loadCurrentTenant();
        if (est.isEmpty()) return "error-tenant";

        try {
            if (message == null || message.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Message requis.");
                return "redirect:/tenant/dashboard";
            }

            SupportTicket ticket = ticketRepository.findById( ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable"));
            if (!ticket.getEstablishment().getId().equals(est.get().getId())) {
                throw new IllegalStateException("Ticket hors de ce locataire.");
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null && auth.getName() != null ? auth.getName() : "SYSTEM";
            String roleLabel = resolveRoleLabel(auth, username);

            messageRepository.save(new SupportTicketMessage(ticket, username, roleLabel, message.trim()));
            ticket.setUpdatedBy(username);
            ticketRepository.save(ticket);

            notifyTicketByEmail(est.get(), ticket, message.trim(), username);
            redirectAttributes.addFlashAttribute("success", "Message ajoute.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur message : " + ex.getMessage());
        }
        return "redirect:/tenant/dashboard";
    }

    private Optional<Establishment> loadCurrentTenant() {
        Long id = TenantContext.getCurrentTenantId();
        if (id == null) return Optional.empty();
        return establishmentRepository.findById(id);
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

    private void notifyTicketByEmail(Establishment est, SupportTicket ticket, String message, String author) {
        if (est == null) {
            return;
        }
        List<String> recipients = new ArrayList<>();
        addRoleEmails(recipients, userRepository.findByEstablishmentIdIsNullAndRoles_NameIn(List.of("SUPER_ADMIN")));
        addRoleEmails(recipients, userRepository.findByEstablishmentIdAndRoles_NameIn(est.getId(), List.of("DIRECTEUR", "COMPTABLE")));
        if (recipients.isEmpty()) {
            return;
        }
        String subject = "OSM - Ticket " + ticket.getId() + " (" + ticket.getSubject() + ")";
        String body = "Etablissement: " + est.getName() + "\n"
                + "Categorie: " + ticket.getCategory().name() + "\n"
                + "Auteur: " + author + "\n\n"
                + message;
        for (String email : recipients) {
            emailService.sendForTenant(est, email, subject, body);
        }
    }

    private void addRoleEmails(List<String> out, List<User> users) {
        if (users == null) {
            return;
        }
        for (User user : users) {
            String email = user != null ? user.getUsername() : null;
            if (isValidEmail(email) && !out.contains(email)) {
                out.add(email);
            }
        }
    }

    private boolean isValidEmail(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isBlank()) {
            return false;
        }
        try {
            InternetAddress address = new InternetAddress(trimmed, true);
            address.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}

