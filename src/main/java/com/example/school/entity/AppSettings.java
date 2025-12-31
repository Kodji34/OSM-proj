package com.example.school.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AppSettings {

    @Id
    private Long id = 1L;

    private String themePrimary = "#1E88E5";

    public AppSettings() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThemePrimary() {
        return themePrimary;
    }

    public void setThemePrimary(String themePrimary) {
        this.themePrimary = themePrimary;
    }
}
