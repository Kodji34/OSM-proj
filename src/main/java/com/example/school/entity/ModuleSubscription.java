package com.example.school.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"establishment_id", "module_id"}))
public class ModuleSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Establishment establishment;

    @ManyToOne(optional = false)
    private ModuleDefinition module;

    @Enumerated(EnumType.STRING)
    private ModuleStatus status = ModuleStatus.INACTIVE;

    private LocalDateTime requestedAt;

    private LocalDateTime activatedAt;

    private String activatedBy;

    public ModuleSubscription() {}

    public ModuleSubscription(Establishment establishment, ModuleDefinition module, ModuleStatus status) {
        this.establishment = establishment;
        this.module = module;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }
    public ModuleDefinition getModule() { return module; }
    public void setModule(ModuleDefinition module) { this.module = module; }
    public ModuleStatus getStatus() { return status; }
    public void setStatus(ModuleStatus status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public String getActivatedBy() { return activatedBy; }
    public void setActivatedBy(String activatedBy) { this.activatedBy = activatedBy; }
}
