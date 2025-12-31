package com.example.school.repository;

import com.example.school.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByEstablishmentIdOrderByTitleAsc(Long establishmentId);
    Optional<Subject> findByEstablishmentIdAndTitleIgnoreCase(Long establishmentId, String title);
}
