package com.expensesplitter.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Audit trail entry written by the AuditLogger observer.
 * Example message: "Aman added expense 'Dinner' of Rs 1200.00"
 */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plain id (not a relation) so logs survive even if the expense is deleted
    private Long groupId;

    @Column(nullable = false, length = 500)
    private String message;

    private LocalDateTime createdAt;

    public ActivityLog() {
    }

    public ActivityLog(Long groupId, String message) {
        this.groupId = groupId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
