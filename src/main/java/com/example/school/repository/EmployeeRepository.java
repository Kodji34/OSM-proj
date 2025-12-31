package com.example.school.repository;

import com.example.school.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmailAndEstablishmentId(String email, Long establishmentId);
    List<Employee> findByEstablishmentId(Long establishmentId);
}
