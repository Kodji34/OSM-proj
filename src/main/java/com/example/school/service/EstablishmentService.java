package com.example.school.service;

import com.example.school.entity.*;
import com.example.school.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository repo;
    private final AcademicYearRepository academicYearRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PositionRepository positionRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;
    private final ObjectMapper mapper;
    private final EmailService emailService;
    private final String directionEmail;

    public EstablishmentService(EstablishmentRepository repo, AcademicYearRepository academicYearRepo,
                               UserRepository userRepo, RoleRepository roleRepo,
                               PositionRepository positionRepo, EmployeeRepository employeeRepo,
                               PasswordEncoder encoder, ObjectMapper mapper,
                               EmailService emailService,
                               @Value("${app.direction.email:dassigbe@gmail.com}") String directionEmail) {
        this.repo = repo;
        this.academicYearRepo = academicYearRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.positionRepo = positionRepo;
        this.employeeRepo = employeeRepo;
        this.encoder = encoder;
        this.mapper = mapper;
        this.emailService = emailService;
        this.directionEmail = directionEmail;
    }

    public Optional<Establishment> findById(Long id) { return repo.findById(id); }

    @Transactional
    public ActivationResult activateSubscription(Long id) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = auth != null ? auth.getName() : "SYSTEM";
        if (e.getStatus() != ActivationStatus.VALIDATED) {
            throw new IllegalStateException("Etape 1/3 requise: validation par la Direction EdiTik. Connectez-vous en tant que Direction (ou Superadmin) puis validez.");
        }
        if (!e.isDeployed()) {
            throw new IllegalStateException("Etape 2/3 requise: mise en ligne par le Superadmin. Lancez d'abord le deployement.");
        }
        if (e.isActive()) {
            return new ActivationResult(e, false, null, null, false, null);
        }

        // Set as active
        e.setActive(true);
        e.setStatus(ActivationStatus.ACTIVE);
        e.setActivatedAt(LocalDateTime.now());
        e.setActivatedBy(actor);
        e = repo.save(e);

        // Create default positions for this establishment
        createDefaultPositions(e);
        
        // Create default academic year for this establishment
        createDefaultAcademicYear(e);

        System.out.println("Etablissement active : " + e.getName());
        return new ActivationResult(e, false, null, null, false, null);
    }

    @Transactional
    public Establishment deploy(Long id, String configJson) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        ActivationStatus st = e.getStatus();
        if (st != ActivationStatus.VALIDATED) {
            throw new IllegalStateException("Etape 1/3 requise: validation par la Direction EdiTik. Validez avant la mise en ligne.");
        }
        if (e.isDeployed()) {
            return e; // already deployed, idempotent
        }
        e.setConfig(configJson);
        e.setDeployed(true);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        e.setDeployedBy(auth != null ? auth.getName() : "SYSTEM");
        e.setDeployedAt(LocalDateTime.now());
        // Extract domain/access URL if provided
        try {
            var node = mapper.readTree(configJson);
            String domain = node.path("domain").asText(null);
            if (domain != null && !domain.isBlank()) {
                if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                    domain = "http://" + domain;
                }
                // fallback if domain is malformed
                if (!domain.contains(".")) {
                    domain = "http://localhost:8081";
                }
                e.setAccessUrl(domain);
            }
        } catch (Exception ignored) {
            // fallback to localhost to allow local access
            e.setAccessUrl("http://localhost:8081");
        }
        return repo.save(e);
    }

    @Transactional
    public Establishment deactivate(Long id) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        e.setActive(false);
        e.setDeployed(false);
        e.setStatus(ActivationStatus.SUSPENDED);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        e.setSuspendedAt(LocalDateTime.now());
        e.setSuspendedBy(auth != null ? auth.getName() : "SYSTEM");
        return repo.save(e);
    }

    @Transactional
    public Establishment archive(Long id) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        if (e.isArchived()) {
            return e;
        }
        e.setArchived(true);
        e.setActive(false);
        e.setDeployed(false);
        e.setReady(false);
        e.setStatus(ActivationStatus.SUSPENDED);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        e.setArchivedAt(LocalDateTime.now());
        e.setArchivedBy(auth != null ? auth.getName() : "SYSTEM");
        return repo.save(e);
    }

    @Transactional
    public Establishment validate(Long id) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        if (e.getStatus() == ActivationStatus.ACTIVE) {
            throw new IllegalStateException("Impossible de valider: l'etablissement est deja actif.");
        }
        if (e.getStatus() == ActivationStatus.VALIDATED) {
            return e;
        }
        e.setStatus(ActivationStatus.VALIDATED);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        e.setValidatedAt(LocalDateTime.now());
        e.setValidatedBy(auth != null ? auth.getName() : "SYSTEM");
        return repo.save(e);
    }

    @Transactional
    public Establishment updateSubscriptionPlan(Long id, SubscriptionPlan plan) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        e.setSubscriptionPlan(plan);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        e.setPlanUpdatedAt(LocalDateTime.now());
        e.setPlanUpdatedBy(auth != null ? auth.getName() : "SYSTEM");
        return repo.save(e);
    }

    @Transactional
    public Establishment requestValidation(Long id) {
        Establishment e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Establishment not found"));
        e.setStatus(ActivationStatus.REQUESTED);
        e.setActive(false);
        e.setDeployed(false);

        String loginUrl = "http://localhost:8081/login";
        String subject = "OSM - Demande d'adhesion (" + e.getName() + ")";
        String body = "Bonjour,\n\n" +
                "Une demande d'adhesion a ete soumise.\n\n" +
                "Etablissement : " + e.getName() + "\n" +
                "CFE          : " + e.getCfe() + "\n" +
                "Pays/Ville   : " + e.getCountry() + " / " + e.getCity() + "\n" +
                "Contact      : " + e.getDirectorName() + (e.getDirectorEmail() != null ? " - " + e.getDirectorEmail() : "") + "\n\n" +
                "Connexion (direction) : " + loginUrl + "\n\n" +
                "OSM Open Suku Manager";
        boolean sent = emailService.send(directionEmail, subject, body);
        System.out.println("REQUEST VALIDATION: email " + (sent ? "envoye" : "non envoye") + " a " + directionEmail + " pour " + e.getName());
        return repo.save(e);
    }

    /**
     * Create a default academic year for the establishment
     */
    private void createDefaultAcademicYear(Establishment establishment) {
        // Check if academic year already exists
        String currentYear = getCurrentAcademicYear();
        if (academicYearRepo.findByAcademicYearAndEstablishmentId(currentYear, establishment.getId()).isPresent()) {
            System.out.println("  Annee academique " + currentYear + " deja existante");
            return;
        }

        // Create new academic year (e.g., 2024-2025 from Sept 2024 to Aug 2025)
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 31);

        LocalDate term1Start = startDate;
        LocalDate term1End = startDate.plusMonths(4).minusDays(1);
        LocalDate term2Start = term1End.plusDays(1);
        LocalDate term2End = endDate;

        AcademicYear academicYear = new AcademicYear(
                currentYear,
                startDate,
                endDate,
                PeriodType.SEMESTER,
                term1Start,
                term1End,
                term2Start,
                term2End,
                null,
                null,
                establishment
        );
        academicYearRepo.save(academicYear);
        System.out.println("  Annee academique creee : " + currentYear);
    }

    /**
     * Get current academic year in format "YYYY-YYYY"
     */
    private String getCurrentAcademicYear() {
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        
        // Academic year starts in September (month 9)
        if (currentMonth >= 9) {
            return currentYear + "-" + (currentYear + 1);
        } else {
            return (currentYear - 1) + "-" + currentYear;
        }
    }

    /**
     * Create default positions for the establishment
     */
    private void createDefaultPositions(Establishment establishment) {
        String[] defaultPositions = {
            "Directeur", "Directeur Adjoint", "Secretaire", "Enseignant",
            "Etudiant", "Parent", "Agent de Maintenance", "Comptable"
        };

        for (String positionName : defaultPositions) {
            if (positionRepo.findByName(positionName).isEmpty()) {
                Position position = new Position(positionName, "Position de " + positionName);
                positionRepo.save(position);
                System.out.println("  Position creee : " + positionName);
            }
        }
    }

    public record ActivationResult(
            Establishment establishment,
            boolean emailSent,
            String loginUrl,
            String adminUsername,
            boolean adminAlreadyExists,
            String tempPassword
    ) {}
}
