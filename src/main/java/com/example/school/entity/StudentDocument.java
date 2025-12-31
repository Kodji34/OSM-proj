package com.example.school.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "STUDENT_DOCUMENT")
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(length = 255)
    private String filename;

    @Column(length = 120)
    private String contentType;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    private LocalDateTime uploadedAt;

    public StudentDocument() {}

    public StudentDocument(User student, String filename, String contentType, byte[] data, LocalDateTime uploadedAt) {
        this.student = student;
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
