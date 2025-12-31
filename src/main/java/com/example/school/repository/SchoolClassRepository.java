package com.example.school.repository;

import com.example.school.entity.SchoolClass;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByEstablishmentIdOrderByNameAsc(Long establishmentId);
    List<SchoolClass> findByEstablishmentIdAndAcademicYearIdOrderByNameAsc(Long establishmentId, Long academicYearId);
    List<SchoolClass> findByEstablishmentIdAndRoomId(Long establishmentId, Long roomId);
    Optional<SchoolClass> findByEstablishmentIdAndNameIgnoreCase(Long establishmentId, String name);
}
