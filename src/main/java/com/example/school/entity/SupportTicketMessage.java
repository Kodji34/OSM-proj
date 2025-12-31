package com.example.school.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SupportTicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SupportTicket ticket;

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private String authorRoleLabel;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    public SupportTicketMessage() {}

    public SupportTicketMessage(SupportTicket ticket, String authorUsername, String authorRoleLabel, String message) {
        this.ticket = ticket;
        this.authorUsername = authorUsername;
        this.authorRoleLabel = authorRoleLabel;
        this.message = message;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SupportTicket getTicket() { return ticket; }
    public void setTicket(SupportTicket ticket) { this.ticket = ticket; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getAuthorRoleLabel() { return authorRoleLabel; }
    public void setAuthorRoleLabel(String authorRoleLabel) { this.authorRoleLabel = authorRoleLabel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

