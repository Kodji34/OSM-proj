package com.example.school.repository;

import com.example.school.entity.SupportTicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketMessageRepository extends JpaRepository<SupportTicketMessage, Long> {
    List<SupportTicketMessage> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);
    List<SupportTicketMessage> findByTicket_IdInOrderByCreatedAtAsc(List<Long> ticketIds);
}
