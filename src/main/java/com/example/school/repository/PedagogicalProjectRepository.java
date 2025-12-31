package com.example.school.repository;

import com.example.school.entity.PedagogicalProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedagogicalProjectRepository extends JpaRepository<PedagogicalProject, Long> {
    List<PedagogicalProject> findByEstablishmentIdOrderByIdDesc(Long establishmentId);
    Optional<PedagogicalProject> findByEstablishmentIdAndTitleIgnoreCase(Long establishmentId, String title);
}
