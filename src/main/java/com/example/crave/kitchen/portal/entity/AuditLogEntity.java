package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "ck_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 19, scale = 0)
    private Long id;

    @Column(name = "user_id", precision = 19, scale = 0)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private Action action;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id", precision = 19, scale = 0)
    private Long entityId;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "details", length = 4000)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.SUCCESS;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "error_message", length = 4000)
    private String errorMessage;

    @Column(name = "execution_time_ms", precision = 19, scale = 0)
    private Long executionTimeMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Action {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        LOGIN,
        LOGOUT,
        PASSWORD_CHANGE,
        PASSWORD_RESET,
        EMAIL_VERIFICATION,
        ACCOUNT_LOCKED,
        ACCOUNT_UNLOCKED,
        SUSPICIOUS_ACTIVITY,
        APPROVAL_REQUEST,
        APPROVAL_GRANTED,
        APPROVAL_REJECTED,
        FEATURE_ENABLED,
        FEATURE_DISABLED,
        EXPORT_DATA,
        IMPORT_DATA,
        SYSTEM_BACKUP,
        SYSTEM_RESTORE
    }

    public enum Status {
        SUCCESS,
        FAILED,
        PENDING,
        CANCELLED
    }
}