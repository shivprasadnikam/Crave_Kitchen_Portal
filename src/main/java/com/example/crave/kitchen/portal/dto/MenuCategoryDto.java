package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryDto {

    private Long id;
    private Long vendorId;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isFeatured;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MenuItemDto> menuItems;
    private Integer itemCount;
}