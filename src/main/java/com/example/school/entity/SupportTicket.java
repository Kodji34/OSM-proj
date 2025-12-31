package com.example.school.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Establishment establishment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportTicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportTicketTarget target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportTicketStatus status = SupportTicketStatus.OPEN;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String updatedBy;

    public SupportTicket() {}

    public SupportTicket(Establishment establishment,
                         SupportTicketCategory category,
                         SupportTicketTarget target,
                         String subject,
                         String createdBy) {
        this.establishment = establishment;
        this.category = category;
        this.target = target;
        this.subject = subject;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = createdAt;
        if (status == null) status = SupportTicketStatus.OPEN;
        if (updatedBy == null) updatedBy = createdBy;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }
    public SupportTicketCategory getCategory() { return category; }
    public void setCategory(SupportTicketCategory category) { this.category = category; }
    public SupportTicketTarget getTarget() { return target; }
    public void setTarget(SupportTicketTarget target) { this.target = target; }
    public SupportTicketStatus getStatus() { return status; }
    public void setStatus(SupportTicketStatus status) { this.status = status; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}

