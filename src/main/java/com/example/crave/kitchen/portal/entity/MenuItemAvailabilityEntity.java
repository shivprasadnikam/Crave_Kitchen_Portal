package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "ck_menu_item_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1=Monday, 2=Tuesday, ..., 7=Sunday

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "available_from")
    @Temporal(TemporalType.TIME)
    private LocalTime availableFrom;

    @Column(name = "available_until")
    @Temporal(TemporalType.TIME)
    private LocalTime availableUntil;

    @Column(name = "max_quantity_per_day")
    private Integer maxQuantityPerDay;

    @Column(name = "current_quantity_available")
    private Integer currentQuantityAvailable;

    @Column(name = "is_special_offer", nullable = false)
    private Boolean isSpecialOffer = false;

    @Column(name = "special_offer_price", precision = 10, scale = 2)
    private BigDecimal specialOfferPrice;

    @Column(name = "special_offer_description", length = 500)
    private String specialOfferDescription;

    @Column(name = "special_offer_valid_from")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime specialOfferValidFrom;

    @Column(name = "special_offer_valid_until")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime specialOfferValidUntil;

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
}