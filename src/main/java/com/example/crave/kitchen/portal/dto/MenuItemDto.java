package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {

    private Long id;
    private Long vendorId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean isAvailable;
    private Boolean isFeatured;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;
    private String spiceLevel;
    private Integer preparationTimeMinutes;
    private Integer calories;
    private BigDecimal proteinGrams;
    private BigDecimal carbsGrams;
    private BigDecimal fatGrams;
    private BigDecimal fiberGrams;
    private Integer sodiumMg;
    private BigDecimal sugarGrams;
    private String allergens;
    private String ingredients;
    private String cookingInstructions;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MenuItemImageDto> images;
    private List<MenuItemAvailabilityDto> availability;
    private String primaryImageUrl;
    private Boolean hasDiscount;
    private BigDecimal discountPercentage;
}