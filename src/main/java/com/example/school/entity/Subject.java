package com.example.school.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SUBJECT")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 160, nullable = false)
    private String title;

    private Integer hoursTd;

    private Integer hoursTp;

    private Integer hoursCm;

    private Integer coefficient;

    private String programName;

    private String programContentType;

    @Lob
    @Column(name = "program_data")
    private byte[] programData;

    private boolean archived = false;

    @ManyToMany
    @JoinTable(
            name = "subject_level",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "level_id")
    )
    private Set<Level> levels = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    public Subject() {}

    public Subject(String title, Establishment establishment) {
        this.title = title;
        this.establishment = establishment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getHoursTd() { return hoursTd; }
    public void setHoursTd(Integer hoursTd) { this.hoursTd = hoursTd; }
    public Integer getHoursTp() { return hoursTp; }
    public void setHoursTp(Integer hoursTp) { this.hoursTp = hoursTp; }
    public Integer getHoursCm() { return hoursCm; }
    public void setHoursCm(Integer hoursCm) { this.hoursCm = hoursCm; }
    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
    public String getProgramContentType() { return programContentType; }
    public void setProgramContentType(String programContentType) { this.programContentType = programContentType; }
    public byte[] getProgramData() { return programData; }
    public void setProgramData(byte[] programData) { this.programData = programData; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Set<Level> getLevels() { return levels; }
    public void setLevels(Set<Level> levels) { this.levels = levels; }
    public Establishment getEstablishment() { return establishment; }
    public void setEstablishment(Establishment establishment) { this.establishment = establishment; }
}
