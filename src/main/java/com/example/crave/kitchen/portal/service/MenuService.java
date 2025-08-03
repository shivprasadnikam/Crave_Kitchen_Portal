package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MenuService {

    // =====================================================
    // MENU CATEGORIES
    // =====================================================

    Page<MenuCategoryDto> getAllCategories(Long vendorId, Boolean isActive, Boolean isFeatured, Pageable pageable);

    Optional<MenuCategoryDto> getCategoryById(Long id);

    MenuCategoryDto createCategory(Long vendorId, CreateMenuCategoryRequestDto requestDto);

    Optional<MenuCategoryDto> updateCategory(Long vendorId, Long id, CreateMenuCategoryRequestDto requestDto);

    boolean deleteCategory(Long id);

    // =====================================================
    // MENU ITEMS
    // =====================================================

    Page<MenuItemDto> getAllMenuItems(Long vendorId, Long categoryId, Boolean isAvailable,
            Boolean isFeatured, Boolean isVegetarian, Boolean isVegan,
            Boolean isGlutenFree, Boolean isSpicy, BigDecimal minPrice,
            BigDecimal maxPrice, String searchTerm, Pageable pageable);

    Optional<MenuItemDto> getMenuItemById(Long id);

    MenuItemDto createMenuItem(Long vendorId, CreateMenuItemRequestDto requestDto);

    Optional<MenuItemDto> updateMenuItem(Long vendorId, Long id, CreateMenuItemRequestDto requestDto);

    boolean deleteMenuItem(Long id);

    Page<MenuItemDto> searchMenuItems(Long vendorId, String searchTerm, Pageable pageable);

    // =====================================================
    // MENU ITEM IMAGES
    // =====================================================

    List<MenuItemImageDto> getImagesForMenuItem(Long menuItemId);

    MenuItemImageDto uploadImage(Long menuItemId, MenuItemImageDto imageDto);

    boolean setPrimaryImage(Long menuItemId, Long imageId);

    boolean deleteImage(Long menuItemId, Long imageId);

    // =====================================================
    // MENU ITEM AVAILABILITY
    // =====================================================

    List<MenuItemAvailabilityDto> getAvailabilityForMenuItem(Long menuItemId);

    List<MenuItemAvailabilityDto> updateAvailability(Long menuItemId, List<MenuItemAvailabilityDto> availabilityDtos);

    MenuItemAvailabilityDto createSpecialOffer(Long menuItemId, MenuItemAvailabilityDto specialOfferDto);

    // =====================================================
    // MENU OVERVIEW & ANALYTICS
    // =====================================================

    MenuOverviewDto getMenuOverview(Long vendorId);

    Page<MenuItemDto> getFeaturedItems(Long vendorId, Pageable pageable);

    Page<MenuItemDto> getItemsByDietaryPreference(Long vendorId, String preference, Pageable pageable);
}