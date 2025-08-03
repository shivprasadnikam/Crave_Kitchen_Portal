package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemAvailabilityDto {

    private Long id;
    private Long menuItemId;
    private Integer dayOfWeek; // 1=Monday, 2=Tuesday, ..., 7=Sunday
    private Boolean isAvailable;
    private LocalTime availableFrom;
    private LocalTime availableUntil;
    private Integer maxQuantityPerDay;
    private Integer currentQuantityAvailable;
    private Boolean isSpecialOffer;
    private BigDecimal specialOfferPrice;
    private String specialOfferDescription;
    private LocalDateTime specialOfferValidFrom;
    private LocalDateTime specialOfferValidUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}