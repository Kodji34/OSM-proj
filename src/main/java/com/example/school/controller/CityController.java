package com.example.school.controller;

import com.example.school.entity.City;
import com.example.school.repository.CityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping("/api/cities")
    public List<City> list(@RequestParam(value = "country", required = false) String countryCode) {
        if (countryCode != null && !countryCode.isBlank()) {
            return cityRepository.findByCountryCodeOrderByNameAsc(countryCode);
        }
        return cityRepository.findAll();
    }

    @PostMapping("/api/admin/cities")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String code = payload.get("countryCode");
        if (name == null || name.isBlank() || code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body("Missing name or countryCode");
        }
        if (cityRepository.findByNameAndCountryCode(name, code).isPresent()) {
            return ResponseEntity.status(409).body("City already exists");
        }
        City saved = cityRepository.save(new City(name, code));
        return ResponseEntity.ok(saved);
    }
}
