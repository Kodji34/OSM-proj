package com.example.school.repository;

import com.example.school.entity.SchoolType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolTypeRepository extends JpaRepository<SchoolType, Long> {
    Optional<SchoolType> findByName(String name);
}
