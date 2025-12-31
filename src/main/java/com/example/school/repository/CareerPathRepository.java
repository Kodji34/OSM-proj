package com.example.school.repository;

import com.example.school.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {
    List<CareerPath> findByEstablishmentIdOrderByNameAsc(Long establishmentId);
    Optional<CareerPath> findByEstablishmentIdAndNameIgnoreCase(Long establishmentId, String name);
}
