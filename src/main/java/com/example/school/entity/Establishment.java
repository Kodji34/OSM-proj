package com.example.school.entity;

import jakarta.persistence.*;
import com.example.school.entity.ActivationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 200, unique = true)
    private String slug;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private SchoolType type;

    private String address;

    private boolean active = false;

    private boolean deployed = false;

    @Column(columnDefinition = "TEXT")
    private String config;

    private String directorName;

    private String directorPhone;

    private String directorEmail;

    private String directorCivility;

    private java.time.LocalDate creationDate;

    private String country;

    private String city;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String logoUrl;

    @Column(unique = true, nullable = false)
    private String cfe;

    @Enumerated(EnumType.STRING)
    private ActivationStatus status = ActivationStatus.REQUESTED;

    private LocalDateTime deployedAt;

    private String deployedBy;

    private LocalDateTime validatedAt;

    private String validatedBy;

    private LocalDateTime activatedAt;

    private String activatedBy;

    private LocalDateTime suspendedAt;

    private String suspendedBy;

    private boolean archived = false;

    private LocalDateTime archivedAt;

    private String archivedBy;

    private LocalDateTime planUpdatedAt;

    private String planUpdatedBy;

    private String createdBy;

    private String accessUrl;

    private String themePrimary;

    private String themeSecondary;

    private boolean ready = false;

    private LocalDateTime readyAt;

    private String readyBy;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.BASIC;

    @Column(length = 200)
    private String publicTitle;

    @Column(length = 300)
    private String publicSubtitle;

    @Column(length = 2000)
    private String publicAbout;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String publicHighlightImage;

    @Column(length = 2000)
    private String publicNews;

    @Column(length = 1000)
    private String publicAlerts;

    @Column(length = 2000)
    private String publicStaffIntro;

    @Column(length = 300)
    private String publicElearningUrl;

    private String smtpHost;

    private Integer smtpPort;

    private String smtpUsername;

    @Column(length = 2000)
    private String smtpPassword;

    private String smtpFrom;

    private boolean smtpAuth = true;

    private boolean smtpStarttls = true;

    private boolean smtpEnabled = false;

    public Establishment() {}

    public Establishment(String name, SchoolType type, String address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public SchoolType getType() { return type; }
    public void setType(SchoolType type) { this.type = type; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isDeployed() { return deployed; }
    public void setDeployed(boolean deployed) { this.deployed = deployed; }
    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }
    public String getDirectorName() { return directorName; }
    public void setDirectorName(String directorName) { this.directorName = directorName; }
    public String getDirectorPhone() { return directorPhone; }
    public void setDirectorPhone(String directorPhone) { this.directorPhone = directorPhone; }
    public String getDirectorEmail() { return directorEmail; }
    public void setDirectorEmail(String directorEmail) { this.directorEmail = directorEmail; }
    public String getDirectorCivility() { return directorCivility; }
    public void setDirectorCivility(String directorCivility) { this.directorCivility = directorCivility; }
    public java.time.LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(java.time.LocalDate creationDate) { this.creationDate = creationDate; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getCfe() { return cfe; }
    public void setCfe(String cfe) { this.cfe = cfe; }
    public ActivationStatus getStatus() { return status; }
    public void setStatus(ActivationStatus status) { this.status = status; }
    public LocalDateTime getDeployedAt() { return deployedAt; }
    public void setDeployedAt(LocalDateTime deployedAt) { this.deployedAt = deployedAt; }
    public String getDeployedBy() { return deployedBy; }
    public void setDeployedBy(String deployedBy) { this.deployedBy = deployedBy; }
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public String getActivatedBy() { return activatedBy; }
    public void setActivatedBy(String activatedBy) { this.activatedBy = activatedBy; }
    public LocalDateTime getSuspendedAt() { return suspendedAt; }
    public void setSuspendedAt(LocalDateTime suspendedAt) { this.suspendedAt = suspendedAt; }
    public String getSuspendedBy() { return suspendedBy; }
    public void setSuspendedBy(String suspendedBy) { this.suspendedBy = suspendedBy; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    public String getArchivedBy() { return archivedBy; }
    public void setArchivedBy(String archivedBy) { this.archivedBy = archivedBy; }
    public LocalDateTime getPlanUpdatedAt() { return planUpdatedAt; }
    public void setPlanUpdatedAt(LocalDateTime planUpdatedAt) { this.planUpdatedAt = planUpdatedAt; }
    public String getPlanUpdatedBy() { return planUpdatedBy; }
    public void setPlanUpdatedBy(String planUpdatedBy) { this.planUpdatedBy = planUpdatedBy; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getAccessUrl() { return accessUrl; }
    public void setAccessUrl(String accessUrl) { this.accessUrl = accessUrl; }
    public String getThemePrimary() { return themePrimary; }
    public void setThemePrimary(String themePrimary) { this.themePrimary = themePrimary; }
    public String getThemeSecondary() { return themeSecondary; }
    public void setThemeSecondary(String themeSecondary) { this.themeSecondary = themeSecondary; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
    public LocalDateTime getReadyAt() { return readyAt; }
    public void setReadyAt(LocalDateTime readyAt) { this.readyAt = readyAt; }
    public String getReadyBy() { return readyBy; }
    public void setReadyBy(String readyBy) { this.readyBy = readyBy; }
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public String getPublicTitle() { return publicTitle; }
    public void setPublicTitle(String publicTitle) { this.publicTitle = publicTitle; }
    public String getPublicSubtitle() { return publicSubtitle; }
    public void setPublicSubtitle(String publicSubtitle) { this.publicSubtitle = publicSubtitle; }
    public String getPublicAbout() { return publicAbout; }
    public void setPublicAbout(String publicAbout) { this.publicAbout = publicAbout; }
    public String getPublicHighlightImage() { return publicHighlightImage; }
    public void setPublicHighlightImage(String publicHighlightImage) { this.publicHighlightImage = publicHighlightImage; }
    public String getPublicNews() { return publicNews; }
    public void setPublicNews(String publicNews) { this.publicNews = publicNews; }
    public String getPublicAlerts() { return publicAlerts; }
    public void setPublicAlerts(String publicAlerts) { this.publicAlerts = publicAlerts; }
    public String getPublicStaffIntro() { return publicStaffIntro; }
    public void setPublicStaffIntro(String publicStaffIntro) { this.publicStaffIntro = publicStaffIntro; }
    public String getPublicElearningUrl() { return publicElearningUrl; }
    public void setPublicElearningUrl(String publicElearningUrl) { this.publicElearningUrl = publicElearningUrl; }
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }
    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }
    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }
    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }
    public String getSmtpFrom() { return smtpFrom; }
    public void setSmtpFrom(String smtpFrom) { this.smtpFrom = smtpFrom; }
    public boolean isSmtpAuth() { return smtpAuth; }
    public void setSmtpAuth(boolean smtpAuth) { this.smtpAuth = smtpAuth; }
    public boolean isSmtpStarttls() { return smtpStarttls; }
    public void setSmtpStarttls(boolean smtpStarttls) { this.smtpStarttls = smtpStarttls; }
    public boolean isSmtpEnabled() { return smtpEnabled; }
    public void setSmtpEnabled(boolean smtpEnabled) { this.smtpEnabled = smtpEnabled; }
}
