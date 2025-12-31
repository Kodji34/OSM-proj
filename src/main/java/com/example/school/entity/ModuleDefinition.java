package com.example.school.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ModuleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 512)
    private String description;

    @Enumerated(EnumType.STRING)
    private ModuleCategory category;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ModuleDefinition() {}

    public ModuleDefinition(String code, String name, String description, ModuleCategory category) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ModuleCategory getCategory() { return category; }
    public void setCategory(ModuleCategory category) { this.category = category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Transient
    public boolean isRecentlyCreated() {
        return createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }
}
