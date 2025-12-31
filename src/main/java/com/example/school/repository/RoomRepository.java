package com.example.school.repository;

import com.example.school.entity.Establishment;
import com.example.school.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByEstablishmentIdOrderByNameAsc(Long establishmentId);
    Optional<Room> findByEstablishmentAndNameIgnoreCase(Establishment establishment, String name);
}
