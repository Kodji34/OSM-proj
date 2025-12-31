package com.example.school.repository;

import com.example.school.entity.SupportTicket;
import com.example.school.entity.SupportTicketTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByEstablishment_IdOrderByUpdatedAtDesc(Long establishmentId);
    List<SupportTicket> findByTargetOrderByUpdatedAtDesc(SupportTicketTarget target);
    List<SupportTicket> findAllByOrderByUpdatedAtDesc();
}

