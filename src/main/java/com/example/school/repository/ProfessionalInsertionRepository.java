package com.example.school.repository;

import com.example.school.entity.ProfessionalInsertion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalInsertionRepository extends JpaRepository<ProfessionalInsertion, Long> {
    List<ProfessionalInsertion> findByEstablishmentIdOrderByStartDateDesc(Long establishmentId);
}
