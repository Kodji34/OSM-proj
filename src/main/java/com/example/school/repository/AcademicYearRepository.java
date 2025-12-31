package com.example.school.repository;

import com.example.school.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByAcademicYearAndEstablishmentId(String academicYear, Long establishmentId);
    java.util.List<AcademicYear> findByEstablishmentIdOrderByStartDateDesc(Long establishmentId);
}
