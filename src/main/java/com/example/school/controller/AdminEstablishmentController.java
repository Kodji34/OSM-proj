package com.example.school.controller;

import com.example.school.entity.Establishment;
import com.example.school.entity.SubscriptionPlan;
import com.example.school.service.EstablishmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/establishments")
public class AdminEstablishmentController {

    private final EstablishmentService service;
    private final ObjectMapper mapper;

    public AdminEstablishmentController(EstablishmentService service, ObjectMapper mapper) { 
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ADHESION')")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        try {
            EstablishmentService.ActivationResult result = service.activateSubscription(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ADHESION')")
    public ResponseEntity<Establishment> deactivate(@PathVariable Long id) {
        Establishment e = service.deactivate(id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<Establishment> archive(@PathVariable Long id) {
        Establishment e = service.archive(id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/{id}/deploy")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','DIRECTION')")
    public ResponseEntity<?> deploy(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String configJson = null;
        try {
            configJson = mapper.writeValueAsString(payload);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Configuration deploiement invalide.");
        }
        try {
            Establishment e = service.deploy(id, configJson);
            return ResponseEntity.ok(e);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/direction/{id}/validate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DIRECTION')")
    public ResponseEntity<?> validate(@PathVariable Long id) {
        try {
            Establishment e = service.validate(id);
            return ResponseEntity.ok(e);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/request-validation")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Establishment> requestValidation(@PathVariable Long id) {
        Establishment e = service.requestValidation(id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/{id}/plan")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADHESION')")
    public ResponseEntity<Establishment> updatePlan(@PathVariable Long id, @RequestParam String plan) {
        try {
            SubscriptionPlan parsed = SubscriptionPlan.valueOf(plan.toUpperCase());
            Establishment e = service.updateSubscriptionPlan(id, parsed);
            return ResponseEntity.ok(e);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
