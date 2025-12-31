package com.example.school.repository;

import com.example.school.entity.ModuleStatus;
import com.example.school.entity.ModuleSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleSubscriptionRepository extends JpaRepository<ModuleSubscription, Long> {
    List<ModuleSubscription> findByEstablishmentId(Long establishmentId);
    Optional<ModuleSubscription> findByEstablishmentIdAndModuleId(Long establishmentId, Long moduleId);
    List<ModuleSubscription> findByStatus(ModuleStatus status);
}
