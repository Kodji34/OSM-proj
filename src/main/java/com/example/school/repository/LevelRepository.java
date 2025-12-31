package com.example.school.repository;

import com.example.school.entity.Establishment;
import com.example.school.entity.Level;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findByEstablishmentIdOrderByNameAsc(Long establishmentId);
    Optional<Level> findByEstablishmentAndNameIgnoreCase(Establishment establishment, String name);
}
