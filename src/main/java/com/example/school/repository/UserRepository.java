package com.example.school.repository;

import com.example.school.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByEstablishmentId(Long establishmentId);
    Optional<User> findTopByEstablishmentIdOrderByIdDesc(Long establishmentId);
    List<User> findByEstablishmentIdIsNull();
    List<User> findByEstablishmentIdAndRoles_NameIn(Long establishmentId, List<String> roleNames);
    List<User> findByEstablishmentIdIsNullAndRoles_NameIn(List<String> roleNames);
    long countByEstablishmentIdAndRoles_NameInAndSchoolClass_Id(Long establishmentId, List<String> roleNames, Long schoolClassId);
    long countByLastLoginAtAfterAndEnabledIsTrue(LocalDateTime cutoff);
}
