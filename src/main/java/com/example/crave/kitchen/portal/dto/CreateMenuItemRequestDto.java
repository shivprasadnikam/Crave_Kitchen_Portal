package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuItemRequestDto {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Item name is required")
    @Size(max = 255, message = "Item name cannot exceed 255 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private Boolean isAvailable = true;
    private Boolean isFeatured = false;
    private Boolean isVegetarian = false;
    private Boolean isVegan = false;
    private Boolean isGlutenFree = false;
    private Boolean isSpicy = false;

    @Size(max = 50, message = "Spice level description cannot exceed 50 characters")
    private String spiceLevel = "none";

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTimeMinutes;

    @Min(value = 0, message = "Calories must be 0 or greater")
    private Integer calories;

    @DecimalMin(value = "0.0", message = "Protein grams must be 0 or greater")
    private BigDecimal proteinGrams;

    @DecimalMin(value = "0.0", message = "Carbs grams must be 0 or greater")
    private BigDecimal carbsGrams;

    @DecimalMin(value = "0.0", message = "Fat grams must be 0 or greater")
    private BigDecimal fatGrams;

    @DecimalMin(value = "0.0", message = "Fiber grams must be 0 or greater")
    private BigDecimal fiberGrams;

    @Min(value = 0, message = "Sodium must be 0 or greater")
    private Integer sodiumMg;

    @DecimalMin(value = "0.0", message = "Sugar grams must be 0 or greater")
    private BigDecimal sugarGrams;

    @Size(max = 500, message = "Allergens cannot exceed 500 characters")
    private String allergens;

    @Size(max = 2000, message = "Ingredients cannot exceed 2000 characters")
    private String ingredients;

    @Size(max = 2000, message = "Cooking instructions cannot exceed 2000 characters")
    private String cookingInstructions;

    @Min(value = 0, message = "Display order must be 0 or greater")
    private Integer displayOrder = 0;

    private List<MenuItemImageDto> images;
    private List<MenuItemAvailabilityDto> availability;
}