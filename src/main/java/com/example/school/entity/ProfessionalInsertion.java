package com.example.school.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "PROFESSIONAL_INSERTION")
public class ProfessionalInsertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String type;

    @Column(length = 180, nullable = false)
    private String partnerName;

    @Column(length = 180)
    private String title;

    @Column(length = 120)
    private String levelOrClass;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 120)
    private String responsible;

    @Column(length = 120)
    private String contactName;

    @Column(length = 160)
    private String contactEmail;

    @Column(length = 40)
    private String contactPhone;

    @Column(length = 1000)
    private String description;

    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    public ProfessionalInsertion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLevelOrClass() { return levelOrClass; }
    public void setLevelOrClass(String levelOrClass) { this.levelOrClass = levelOrClass; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }
}
