package com.example.school.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CITY", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "country_code"}))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    public City() {}

    public City(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
