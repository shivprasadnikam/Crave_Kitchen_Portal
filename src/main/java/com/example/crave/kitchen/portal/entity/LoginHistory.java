package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ck_login_history")
@Data
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", columnDefinition = "ENUM('mobile', 'desktop', 'tablet')")
    private DeviceType deviceType;

    @Column(name = "is_successful", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isSuccessful = true;

    @Column(name = "failure_reason")
    private String failureReason;

    @PrePersist
    protected void onCreate() {
        loginAt = LocalDateTime.now();
    }

    // Device Type Enum
    public enum DeviceType {
        mobile, desktop, tablet
    }
}