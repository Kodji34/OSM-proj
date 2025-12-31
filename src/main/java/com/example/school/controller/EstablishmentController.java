package com.example.school.controller;

import com.example.school.entity.Establishment;
import com.example.school.repository.EstablishmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    private final EstablishmentRepository repo;

    public EstablishmentController(EstablishmentRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Establishment> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Establishment> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Establishment create(@RequestBody Establishment e) { return repo.save(e); }

    @PutMapping("/{id}")
    public ResponseEntity<Establishment> update(@PathVariable Long id, @RequestBody Establishment e) {
        return repo.findById(id).map(existing -> {
            existing.setName(e.getName());
            existing.setAddress(e.getAddress());
            existing.setType(e.getType());
            repo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTION')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repo.existsById(id)) { repo.deleteById(id); return ResponseEntity.noContent().build(); }
        return ResponseEntity.notFound().build();
    }
}
