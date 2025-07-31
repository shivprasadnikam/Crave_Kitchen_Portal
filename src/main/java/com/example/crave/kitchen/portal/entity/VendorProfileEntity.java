package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "ck_vendor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private VendorsEntity vendorsEntity;

    @Column(name = "restaurant_name", nullable = false, unique = true, length = 255)
    private String restaurantName;

    @Column(name = "cuisine_type", length = 100)
    private String cuisineType;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "street_address", length = 500)
    private String streetAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime approvalDate;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejection_reason", length = 4000)
    private String rejectionReason;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "featured_until")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime featuredUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        SUSPENDED
    }
}