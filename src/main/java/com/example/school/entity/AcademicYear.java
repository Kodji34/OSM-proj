package com.example.school.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String academicYear; // Format: "2024-2025"

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column(nullable = false)
    private LocalDate term1Start;

    @Column(nullable = false)
    private LocalDate term1End;

    @Column(nullable = false)
    private LocalDate term2Start;

    @Column(nullable = false)
    private LocalDate term2End;

    private LocalDate term3Start;

    private LocalDate term3End;

    @Column(nullable = false)
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    public AcademicYear() {}

    public AcademicYear(String academicYear,
                        LocalDate startDate,
                        LocalDate endDate,
                        PeriodType periodType,
                        LocalDate term1Start,
                        LocalDate term1End,
                        LocalDate term2Start,
                        LocalDate term2End,
                        LocalDate term3Start,
                        LocalDate term3End,
                        Establishment establishment) {
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodType = periodType;
        this.term1Start = term1Start;
        this.term1End = term1End;
        this.term2Start = term2Start;
        this.term2End = term2End;
        this.term3Start = term3Start;
        this.term3End = term3End;
        this.establishment = establishment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public LocalDate getTerm1Start() {
        return term1Start;
    }

    public void setTerm1Start(LocalDate term1Start) {
        this.term1Start = term1Start;
    }

    public LocalDate getTerm1End() {
        return term1End;
    }

    public void setTerm1End(LocalDate term1End) {
        this.term1End = term1End;
    }

    public LocalDate getTerm2Start() {
        return term2Start;
    }

    public void setTerm2Start(LocalDate term2Start) {
        this.term2Start = term2Start;
    }

    public LocalDate getTerm2End() {
        return term2End;
    }

    public void setTerm2End(LocalDate term2End) {
        this.term2End = term2End;
    }

    public LocalDate getTerm3Start() {
        return term3Start;
    }

    public void setTerm3Start(LocalDate term3Start) {
        this.term3Start = term3Start;
    }

    public LocalDate getTerm3End() {
        return term3End;
    }

    public void setTerm3End(LocalDate term3End) {
        this.term3End = term3End;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
