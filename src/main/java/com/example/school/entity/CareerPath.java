package com.example.school.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "career_path",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"establishment_id", "name"})
        }
)
public class CareerPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    @ManyToMany
    @JoinTable(
            name = "career_path_subject",
            joinColumns = @JoinColumn(name = "career_path_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "career_path_project",
            joinColumns = @JoinColumn(name = "career_path_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private Set<PedagogicalProject> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "career_path_insertion",
            joinColumns = @JoinColumn(name = "career_path_id"),
            inverseJoinColumns = @JoinColumn(name = "insertion_id")
    )
    private Set<ProfessionalInsertion> insertions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "career_path_student",
            joinColumns = @JoinColumn(name = "career_path_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> students = new HashSet<>();

    public CareerPath() {}

    public CareerPath(String name, Establishment establishment) {
        this.name = name;
        this.establishment = establishment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<PedagogicalProject> getProjects() {
        return projects;
    }

    public void setProjects(Set<PedagogicalProject> projects) {
        this.projects = projects;
    }

    public Set<ProfessionalInsertion> getInsertions() {
        return insertions;
    }

    public void setInsertions(Set<ProfessionalInsertion> insertions) {
        this.insertions = insertions;
    }

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }
}
