package com.example.school.repository;

import com.example.school.entity.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    Optional<Establishment> findByAccessUrlIgnoreCase(String accessUrl);
    List<Establishment> findByNameIgnoreCase(String name);
    List<Establishment> findByCfeIgnoreCase(String cfe);
    Optional<Establishment> findBySlugIgnoreCase(String slug);
}
