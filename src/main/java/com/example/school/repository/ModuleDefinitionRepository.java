package com.example.school.repository;

import com.example.school.entity.ModuleCategory;
import com.example.school.entity.ModuleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleDefinitionRepository extends JpaRepository<ModuleDefinition, Long> {
    Optional<ModuleDefinition> findByCode(String code);
    List<ModuleDefinition> findByCategory(ModuleCategory category);
}
