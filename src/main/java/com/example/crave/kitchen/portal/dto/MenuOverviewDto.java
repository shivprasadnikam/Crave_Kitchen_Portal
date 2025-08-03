package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOverviewDto {

    private Long vendorId;
    private String vendorName;
    private Integer totalCategories;
    private Integer activeCategories;
    private Integer totalItems;
    private Integer availableItems;
    private Integer featuredItems;
    private Integer vegetarianItems;
    private Integer veganItems;
    private Integer glutenFreeItems;
    private List<MenuCategoryDto> categories;
    private List<MenuItemDto> featuredItemsList;
    private List<MenuItemDto> recentItems;
}