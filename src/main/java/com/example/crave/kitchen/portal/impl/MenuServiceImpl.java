package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.dto.*;
import com.example.crave.kitchen.portal.entity.*;
import com.example.crave.kitchen.portal.repository.*;
import com.example.crave.kitchen.portal.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemImageRepository menuItemImageRepository;
    private final MenuItemAvailabilityRepository menuItemAvailabilityRepository;

    // =====================================================
    // MENU CATEGORIES
    // =====================================================

    @Override
    public Page<MenuCategoryDto> getAllCategories(Long vendorId, Boolean isActive, Boolean isFeatured,
            Pageable pageable) {
        log.info("=== MENU SERVICE - GET ALL CATEGORIES ===");
        log.info("Parameters - vendorId: {}, isActive: {}, isFeatured: {}, page: {}, size: {}",
                vendorId, isActive, isFeatured, pageable.getPageNumber(), pageable.getPageSize());

        try {
            List<MenuCategoryEntity> categories;
            long totalElements;
            String filterType = "ALL";
            
            // Calculate pagination parameters
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int minRow = pageNumber * pageSize;
            int maxRow = (pageNumber + 1) * pageSize;
            
            log.debug("Pagination parameters - minRow: {}, maxRow: {}, pageSize: {}", minRow, maxRow, pageSize);
            
            if (isActive != null && isFeatured != null) {
                categories = menuCategoryRepository.findByVendorIdAndIsActiveAndIsFeaturedWithPagination(vendorId, isActive,
                        isFeatured, minRow, maxRow);
                totalElements = menuCategoryRepository.countByVendorIdAndIsActiveAndIsFeaturedNative(vendorId, isActive, isFeatured);
                filterType = "ACTIVE_AND_FEATURED";
            } else if (isActive != null) {
                categories = menuCategoryRepository.findByVendorIdAndIsActiveWithPagination(vendorId, isActive, minRow, maxRow);
                totalElements = menuCategoryRepository.countByVendorIdAndIsActiveNative(vendorId, isActive);
                filterType = "ACTIVE_ONLY";
            } else if (isFeatured != null) {
                categories = menuCategoryRepository.findByVendorIdAndIsFeaturedWithPagination(vendorId, isFeatured, minRow, maxRow);
                totalElements = menuCategoryRepository.countByVendorIdAndIsFeaturedNative(vendorId, isFeatured);
                filterType = "FEATURED_ONLY";
            } else {
                categories = menuCategoryRepository.findByVendorIdWithPagination(vendorId, minRow, maxRow);
                totalElements = menuCategoryRepository.countByVendorIdNative(vendorId);
                filterType = "ALL";
            }

            log.debug("Database query executed with filter type: {}", filterType);
            
            // Convert to DTOs
            List<MenuCategoryDto> categoryDtos = categories.stream()
                    .map(this::convertToCategoryDto)
                    .collect(Collectors.toList());
            
            // Create Page object manually
            Page<MenuCategoryDto> result = new PageImpl<>(categoryDtos, pageable, totalElements);
            
            log.info("=== SUCCESS ===");
            log.info("Retrieved {} categories for vendorId: {} (Page {} of {})", 
                    result.getTotalElements(), vendorId, pageable.getPageNumber() + 1, result.getTotalPages());
            log.info("Categories in current page: {}", result.getContent().size());
            
            return result;
        } catch (Exception e) {
            log.error("=== ERROR ===");
            log.error("Failed to fetch menu categories for vendorId: {}", vendorId, e);
            log.error("Error type: {}", e.getClass().getSimpleName());
            throw e;
        }
    }

    @Override
    public Optional<MenuCategoryDto> getCategoryById(Long id) {
        log.info("Fetching menu category by ID: {}", id);

        try {
            Optional<MenuCategoryEntity> category = menuCategoryRepository.findById(id);
            if (category.isPresent()) {
                log.info("Successfully found menu category with ID: {}", id);
                return category.map(this::convertToCategoryDto);
            } else {
                log.warn("Menu category not found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error fetching menu category with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public MenuCategoryDto createCategory(Long vendorId, CreateMenuCategoryRequestDto requestDto) {
        log.info("=== MENU SERVICE - CREATE CATEGORY ===");
        log.info("Creating category - Name: '{}', VendorId: {}", requestDto.getName(), vendorId);
        log.info("Category details - Description: '{}', DisplayOrder: {}, IsActive: {}, IsFeatured: {}",
                requestDto.getDescription() != null ? requestDto.getDescription().substring(0, Math.min(50, requestDto.getDescription().length())) + "..." : "null",
                requestDto.getDisplayOrder(), requestDto.getIsActive(), requestDto.getIsFeatured());

        try {
            MenuCategoryEntity category = new MenuCategoryEntity();
            category.setVendorId(vendorId);
            category.setName(requestDto.getName());
            category.setDescription(requestDto.getDescription());
            category.setDisplayOrder(requestDto.getDisplayOrder());
            category.setIsActive(requestDto.getIsActive());
            category.setIsFeatured(requestDto.getIsFeatured());
            category.setImageUrl(requestDto.getImageUrl());
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());

            log.debug("Category entity prepared for persistence");
            MenuCategoryEntity savedCategory = menuCategoryRepository.save(category);
            
            log.info("=== SUCCESS ===");
            log.info("Category created successfully - ID: {}, Name: '{}', VendorId: {}", 
                    savedCategory.getId(), savedCategory.getName(), savedCategory.getVendorId());
            log.info("Category created at: {}", savedCategory.getCreatedAt());

            return convertToCategoryDto(savedCategory);
        } catch (Exception e) {
            log.error("=== ERROR ===");
            log.error("Failed to create category: '{}' for vendorId: {}", requestDto.getName(), vendorId, e);
            log.error("Error type: {}", e.getClass().getSimpleName());
            log.error("Error message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MenuCategoryDto> updateCategory(Long vendorId, Long id, CreateMenuCategoryRequestDto requestDto) {
        log.info("Updating menu category with ID: {} for vendorId: {}", id, vendorId);

        try {
            Optional<MenuCategoryEntity> existingCategory = menuCategoryRepository.findById(id);
            if (existingCategory.isPresent()) {
                MenuCategoryEntity category = existingCategory.get();

                // Verify the category belongs to the vendor
                if (!category.getVendorId().equals(vendorId)) {
                    log.warn("Category with ID: {} does not belong to vendorId: {}", id, vendorId);
                    return Optional.empty();
                }

                category.setName(requestDto.getName());
                category.setDescription(requestDto.getDescription());
                category.setDisplayOrder(requestDto.getDisplayOrder());
                category.setIsActive(requestDto.getIsActive());
                category.setIsFeatured(requestDto.getIsFeatured());
                category.setImageUrl(requestDto.getImageUrl());
                category.setUpdatedAt(LocalDateTime.now());

                MenuCategoryEntity updatedCategory = menuCategoryRepository.save(category);
                log.info("Successfully updated menu category with ID: {} and name: {}",
                        updatedCategory.getId(), updatedCategory.getName());

                return Optional.of(convertToCategoryDto(updatedCategory));
            } else {
                log.warn("Menu category not found for update with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error updating menu category with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        log.info("Deleting menu category with ID: {}", id);

        try {
            if (menuCategoryRepository.existsById(id)) {
                // Check if category has menu items
                long itemCount = menuItemRepository.countByCategoryId(id);
                if (itemCount > 0) {
                    log.warn("Cannot delete category with ID: {} - it has {} associated menu items", id, itemCount);
                    return false;
                }

                menuCategoryRepository.deleteById(id);
                log.info("Successfully deleted menu category with ID: {}", id);
                return true;
            } else {
                log.warn("Menu category not found for deletion with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting menu category with ID: {}", id, e);
            throw e;
        }
    }

    // =====================================================
    // MENU ITEMS
    // =====================================================

    @Override
    public Page<MenuItemDto> getAllMenuItems(Long vendorId, Long categoryId, Boolean isAvailable,
            Boolean isFeatured, Boolean isVegetarian, Boolean isVegan,
            Boolean isGlutenFree, Boolean isSpicy, BigDecimal minPrice,
            BigDecimal maxPrice, String searchTerm, Pageable pageable) {
        log.info("Fetching menu items for vendorId: {}, categoryId: {}, isAvailable: {}, isFeatured: {}, " +
                "isVegetarian: {}, isVegan: {}, isGlutenFree: {}, isSpicy: {}, minPrice: {}, maxPrice: {}, " +
                "searchTerm: {}, page: {}, size: {}",
                vendorId, categoryId, isAvailable, isFeatured, isVegetarian, isVegan, isGlutenFree,
                isSpicy, minPrice, maxPrice, searchTerm, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<MenuItemEntity> items;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                items = menuItemRepository.findByVendorIdAndSearchTerm(vendorId, searchTerm.trim(), pageable);
            } else {
                items = menuItemRepository.findMenuItemsWithFilters(vendorId, categoryId, isVegetarian,
                        isVegan, isGlutenFree, isSpicy, minPrice, maxPrice, searchTerm, pageable);
            }

            Page<MenuItemDto> result = items.map(this::convertToMenuItemDto);
            log.info("Successfully fetched {} menu items for vendorId: {}", result.getTotalElements(), vendorId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching menu items for vendorId: {}", vendorId, e);
            throw e;
        }
    }

    @Override
    public Optional<MenuItemDto> getMenuItemById(Long id) {
        log.info("Fetching menu item by ID: {}", id);

        try {
            Optional<MenuItemEntity> item = menuItemRepository.findById(id);
            if (item.isPresent()) {
                log.info("Successfully found menu item with ID: {}", id);
                return item.map(this::convertToMenuItemDto);
            } else {
                log.warn("Menu item not found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error fetching menu item with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public MenuItemDto createMenuItem(Long vendorId, CreateMenuItemRequestDto requestDto) {
        log.info("=== MENU SERVICE - CREATE MENU ITEM ===");
        log.info("Creating menu item - Name: '{}', VendorId: {}, CategoryId: {}", 
                requestDto.getName(), vendorId, requestDto.getCategoryId());
        log.info("Item details - Price: {}, IsAvailable: {}, IsFeatured: {}", 
                requestDto.getPrice(), requestDto.getIsAvailable(), requestDto.getIsFeatured());
        log.info("Dietary info - Vegetarian: {}, Vegan: {}, GlutenFree: {}, Spicy: {}", 
                requestDto.getIsVegetarian(), requestDto.getIsVegan(), requestDto.getIsGlutenFree(), requestDto.getIsSpicy());

        try {
            // Validate category exists and belongs to the vendor
            log.debug("Validating category with ID: {} for vendorId: {}", requestDto.getCategoryId(), vendorId);
            Optional<MenuCategoryEntity> category = menuCategoryRepository.findById(requestDto.getCategoryId());
            if (category.isEmpty() || !category.get().getVendorId().equals(vendorId)) {
                log.error("=== VALIDATION ERROR ===");
                log.error("Category not found with ID: {} for vendorId: {} for menu item: '{}'",
                        requestDto.getCategoryId(), vendorId, requestDto.getName());
                if (category.isPresent()) {
                    log.error("Category exists but belongs to different vendor: {}", category.get().getVendorId());
                }
                throw new IllegalArgumentException("Category not found with ID: " + requestDto.getCategoryId());
            }
            log.debug("Category validation successful - Category: '{}'", category.get().getName());

            MenuItemEntity item = new MenuItemEntity();
            item.setVendorId(vendorId);
            item.setCategoryId(requestDto.getCategoryId());
            item.setName(requestDto.getName());
            item.setDescription(requestDto.getDescription());
            item.setPrice(requestDto.getPrice());
            item.setOriginalPrice(requestDto.getOriginalPrice());
            item.setIsAvailable(requestDto.getIsAvailable());
            item.setIsFeatured(requestDto.getIsFeatured());
            item.setIsVegetarian(requestDto.getIsVegetarian());
            item.setIsVegan(requestDto.getIsVegan());
            item.setIsGlutenFree(requestDto.getIsGlutenFree());
            item.setIsSpicy(requestDto.getIsSpicy());
            item.setCalories(requestDto.getCalories());
            item.setProteinGrams(requestDto.getProteinGrams());
            item.setCarbsGrams(requestDto.getCarbsGrams());
            item.setFatGrams(requestDto.getFatGrams());
            item.setAllergens(requestDto.getAllergens());
            item.setIngredients(requestDto.getIngredients());
            item.setCookingInstructions(requestDto.getCookingInstructions());
            item.setPreparationTimeMinutes(requestDto.getPreparationTimeMinutes());
            item.setDisplayOrder(requestDto.getDisplayOrder());
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());

            log.debug("Menu item entity prepared for persistence");
            MenuItemEntity savedItem = menuItemRepository.save(item);
            
            log.info("=== SUCCESS ===");
            log.info("Menu item created successfully - ID: {}, Name: '{}', VendorId: {}, CategoryId: {}", 
                    savedItem.getId(), savedItem.getName(), savedItem.getVendorId(), savedItem.getCategoryId());
            log.info("Item created at: {}", savedItem.getCreatedAt());

            return convertToMenuItemDto(savedItem);
        } catch (Exception e) {
            log.error("=== ERROR ===");
            log.error("Failed to create menu item: '{}' for vendorId: {}", requestDto.getName(), vendorId, e);
            log.error("Error type: {}", e.getClass().getSimpleName());
            log.error("Error message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MenuItemDto> updateMenuItem(Long vendorId, Long id, CreateMenuItemRequestDto requestDto) {
        log.info("Updating menu item with ID: {} for vendorId: {}", id, vendorId);

        try {
            Optional<MenuItemEntity> existingItem = menuItemRepository.findById(id);
            if (existingItem.isPresent()) {
                MenuItemEntity item = existingItem.get();

                // Verify the item belongs to the vendor
                if (!item.getVendorId().equals(vendorId)) {
                    log.warn("Menu item with ID: {} does not belong to vendorId: {}", id, vendorId);
                    return Optional.empty();
                }

                // Validate category exists and belongs to the vendor
                Optional<MenuCategoryEntity> category = menuCategoryRepository.findById(requestDto.getCategoryId());
                if (category.isEmpty() || !category.get().getVendorId().equals(vendorId)) {
                    log.error("Category not found with ID: {} for vendorId: {} for menu item update: {}",
                            requestDto.getCategoryId(), vendorId, id);
                    throw new IllegalArgumentException("Category not found with ID: " + requestDto.getCategoryId());
                }

                item.setCategoryId(requestDto.getCategoryId());
                item.setName(requestDto.getName());
                item.setDescription(requestDto.getDescription());
                item.setPrice(requestDto.getPrice());
                item.setOriginalPrice(requestDto.getOriginalPrice());
                item.setIsAvailable(requestDto.getIsAvailable());
                item.setIsFeatured(requestDto.getIsFeatured());
                item.setIsVegetarian(requestDto.getIsVegetarian());
                item.setIsVegan(requestDto.getIsVegan());
                item.setIsGlutenFree(requestDto.getIsGlutenFree());
                item.setIsSpicy(requestDto.getIsSpicy());
                item.setCalories(requestDto.getCalories());
                item.setProteinGrams(requestDto.getProteinGrams());
                item.setCarbsGrams(requestDto.getCarbsGrams());
                item.setFatGrams(requestDto.getFatGrams());
                item.setAllergens(requestDto.getAllergens());
                item.setIngredients(requestDto.getIngredients());
                item.setCookingInstructions(requestDto.getCookingInstructions());
                item.setPreparationTimeMinutes(requestDto.getPreparationTimeMinutes());
                item.setDisplayOrder(requestDto.getDisplayOrder());
                item.setUpdatedAt(LocalDateTime.now());

                MenuItemEntity updatedItem = menuItemRepository.save(item);
                log.info("Successfully updated menu item with ID: {} and name: {}",
                        updatedItem.getId(), updatedItem.getName());

                return Optional.of(convertToMenuItemDto(updatedItem));
            } else {
                log.warn("Menu item not found for update with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error updating menu item with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteMenuItem(Long id) {
        log.info("Deleting menu item with ID: {}", id);

        try {
            if (menuItemRepository.existsById(id)) {
                // Delete associated images and availability records
                menuItemImageRepository.deleteByMenuItemId(id);
                menuItemAvailabilityRepository.deleteByMenuItemId(id);
                menuItemRepository.deleteById(id);

                log.info("Successfully deleted menu item with ID: {} and all associated data", id);
                return true;
            } else {
                log.warn("Menu item not found for deletion with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting menu item with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<MenuItemDto> searchMenuItems(Long vendorId, String searchTerm, Pageable pageable) {
        log.info("Searching menu items for vendorId: {} with search term: '{}', page: {}, size: {}",
                vendorId, searchTerm, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<MenuItemEntity> items = menuItemRepository.findByVendorIdAndSearchTerm(vendorId, searchTerm, pageable);
            Page<MenuItemDto> result = items.map(this::convertToMenuItemDto);
            log.info("Search completed successfully. Found {} items for search term: '{}'",
                    result.getTotalElements(), searchTerm);
            return result;
        } catch (Exception e) {
            log.error("Error searching menu items for vendorId: {} with search term: '{}'", vendorId, searchTerm, e);
            throw e;
        }
    }

    // =====================================================
    // MENU ITEM IMAGES
    // =====================================================

    @Override
    public List<MenuItemImageDto> getImagesForMenuItem(Long menuItemId) {
        log.info("Fetching images for menu item ID: {}", menuItemId);

        try {
            List<MenuItemImageEntity> images = menuItemImageRepository
                    .findByMenuItemIdOrderByDisplayOrderAsc(menuItemId);
            List<MenuItemImageDto> result = images.stream()
                    .map(this::convertToMenuItemImageDto)
                    .collect(Collectors.toList());
            log.info("Successfully fetched {} images for menu item ID: {}", result.size(), menuItemId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching images for menu item ID: {}", menuItemId, e);
            throw e;
        }
    }

    @Override
    public MenuItemImageDto uploadImage(Long menuItemId, MenuItemImageDto imageDto) {
        log.info("Uploading image for menu item ID: {}", menuItemId);

        try {
            MenuItemImageEntity image = new MenuItemImageEntity();
            image.setMenuItemId(menuItemId);
            image.setImageUrl(imageDto.getImageUrl());
            image.setImageType(imageDto.getImageType());
            image.setFileName(imageDto.getFileName());
            image.setFileSizeBytes(imageDto.getFileSizeBytes());
            image.setWidthPixels(imageDto.getWidthPixels());
            image.setHeightPixels(imageDto.getHeightPixels());
            image.setIsPrimary(imageDto.getIsPrimary());
            image.setDisplayOrder(imageDto.getDisplayOrder());
            image.setAltText(imageDto.getAltText());
            image.setCreatedAt(LocalDateTime.now());
            image.setUpdatedAt(LocalDateTime.now());

            // If this is primary image, unset other primary images
            if (Boolean.TRUE.equals(imageDto.getIsPrimary())) {
                menuItemImageRepository.clearPrimaryImage(menuItemId);
            }

            MenuItemImageEntity savedImage = menuItemImageRepository.save(image);
            log.info("Successfully uploaded image with ID: {} for menu item ID: {}",
                    savedImage.getId(), menuItemId);

            return convertToMenuItemImageDto(savedImage);
        } catch (Exception e) {
            log.error("Error uploading image for menu item ID: {}", menuItemId, e);
            throw e;
        }
    }

    @Override
    public boolean setPrimaryImage(Long menuItemId, Long imageId) {
        log.info("Setting primary image for menu item ID: {} with image ID: {}", menuItemId, imageId);

        try {
            // Clear existing primary images
            menuItemImageRepository.clearPrimaryImage(menuItemId);

            // Set new primary image
            int updatedRows = menuItemImageRepository.setPrimaryImage(menuItemId, imageId);
            if (updatedRows > 0) {
                log.info("Successfully set primary image for menu item ID: {} with image ID: {}", menuItemId, imageId);
                return true;
            } else {
                log.warn("Failed to set primary image - image not found for menu item ID: {} with image ID: {}",
                        menuItemId, imageId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error setting primary image for menu item ID: {} with image ID: {}", menuItemId, imageId, e);
            throw e;
        }
    }

    @Override
    public boolean deleteImage(Long menuItemId, Long imageId) {
        log.info("Deleting image with ID: {} for menu item ID: {}", imageId, menuItemId);

        try {
            if (menuItemImageRepository.existsById(imageId)) {
                menuItemImageRepository.deleteById(imageId);
                log.info("Successfully deleted image with ID: {} for menu item ID: {}", imageId, menuItemId);
                return true;
            } else {
                log.warn("Image not found for deletion with ID: {} for menu item ID: {}", imageId, menuItemId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting image with ID: {} for menu item ID: {}", imageId, menuItemId, e);
            throw e;
        }
    }

    // =====================================================
    // MENU ITEM AVAILABILITY
    // =====================================================

    @Override
    public List<MenuItemAvailabilityDto> getAvailabilityForMenuItem(Long menuItemId) {
        log.info("Fetching availability for menu item ID: {}", menuItemId);

        try {
            List<MenuItemAvailabilityEntity> availability = menuItemAvailabilityRepository.findByMenuItemId(menuItemId);
            List<MenuItemAvailabilityDto> result = availability.stream()
                    .map(this::convertToMenuItemAvailabilityDto)
                    .collect(Collectors.toList());
            log.info("Successfully fetched {} availability records for menu item ID: {}",
                    result.size(), menuItemId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching availability for menu item ID: {}", menuItemId, e);
            throw e;
        }
    }

    @Override
    public List<MenuItemAvailabilityDto> updateAvailability(Long menuItemId,
            List<MenuItemAvailabilityDto> availabilityDtos) {
        log.info("Updating availability for menu item ID: {} with {} records", menuItemId, availabilityDtos.size());

        try {
            // Delete existing availability records
            menuItemAvailabilityRepository.deleteByMenuItemId(menuItemId);

            // Create new availability records
            List<MenuItemAvailabilityEntity> availabilityEntities = availabilityDtos.stream()
                    .map(dto -> {
                        MenuItemAvailabilityEntity entity = new MenuItemAvailabilityEntity();
                        entity.setMenuItemId(menuItemId);
                        entity.setDayOfWeek(dto.getDayOfWeek());
                        entity.setIsAvailable(dto.getIsAvailable());
                        entity.setAvailableFrom(dto.getAvailableFrom());
                        entity.setAvailableUntil(dto.getAvailableUntil());
                        entity.setMaxQuantityPerDay(dto.getMaxQuantityPerDay());
                        entity.setCurrentQuantityAvailable(dto.getCurrentQuantityAvailable());
                        entity.setIsSpecialOffer(dto.getIsSpecialOffer());
                        entity.setSpecialOfferPrice(dto.getSpecialOfferPrice());
                        entity.setSpecialOfferDescription(dto.getSpecialOfferDescription());
                        entity.setSpecialOfferValidFrom(dto.getSpecialOfferValidFrom());
                        entity.setSpecialOfferValidUntil(dto.getSpecialOfferValidUntil());
                        entity.setCreatedAt(LocalDateTime.now());
                        entity.setUpdatedAt(LocalDateTime.now());
                        return entity;
                    })
                    .collect(Collectors.toList());

            List<MenuItemAvailabilityEntity> savedEntities = menuItemAvailabilityRepository
                    .saveAll(availabilityEntities);
            List<MenuItemAvailabilityDto> result = savedEntities.stream()
                    .map(this::convertToMenuItemAvailabilityDto)
                    .collect(Collectors.toList());

            log.info("Successfully updated availability for menu item ID: {} with {} records",
                    menuItemId, result.size());
            return result;
        } catch (Exception e) {
            log.error("Error updating availability for menu item ID: {}", menuItemId, e);
            throw e;
        }
    }

    @Override
    public MenuItemAvailabilityDto createSpecialOffer(Long menuItemId, MenuItemAvailabilityDto specialOfferDto) {
        log.info("Creating special offer for menu item ID: {} on day: {}",
                menuItemId, specialOfferDto.getDayOfWeek());

        try {
            MenuItemAvailabilityEntity availability = new MenuItemAvailabilityEntity();
            availability.setMenuItemId(menuItemId);
            availability.setDayOfWeek(specialOfferDto.getDayOfWeek());
            availability.setIsAvailable(true);
            availability.setAvailableFrom(specialOfferDto.getAvailableFrom());
            availability.setAvailableUntil(specialOfferDto.getAvailableUntil());
            availability.setMaxQuantityPerDay(specialOfferDto.getMaxQuantityPerDay());
            availability.setCurrentQuantityAvailable(specialOfferDto.getCurrentQuantityAvailable());
            availability.setIsSpecialOffer(true);
            availability.setSpecialOfferPrice(specialOfferDto.getSpecialOfferPrice());
            availability.setSpecialOfferDescription(specialOfferDto.getSpecialOfferDescription());
            availability.setSpecialOfferValidFrom(specialOfferDto.getSpecialOfferValidFrom());
            availability.setSpecialOfferValidUntil(specialOfferDto.getSpecialOfferValidUntil());
            availability.setCreatedAt(LocalDateTime.now());
            availability.setUpdatedAt(LocalDateTime.now());

            MenuItemAvailabilityEntity savedAvailability = menuItemAvailabilityRepository.save(availability);
            log.info("Successfully created special offer with ID: {} for menu item ID: {}",
                    savedAvailability.getId(), menuItemId);

            return convertToMenuItemAvailabilityDto(savedAvailability);
        } catch (Exception e) {
            log.error("Error creating special offer for menu item ID: {}", menuItemId, e);
            throw e;
        }
    }

    // =====================================================
    // MENU OVERVIEW & ANALYTICS
    // =====================================================

    @Override
    public MenuOverviewDto getMenuOverview(Long vendorId) {
        log.info("Generating menu overview for vendor ID: {}", vendorId);

        try {
            long totalCategories = menuCategoryRepository.countByVendorId(vendorId);
            long totalItems = menuItemRepository.countByVendorId(vendorId);
            long activeCategories = menuCategoryRepository.countByVendorIdAndIsActive(vendorId, true);
            long activeItems = menuItemRepository.countByVendorIdAndIsAvailable(vendorId, true);
            long featuredCategories = menuCategoryRepository.countByVendorIdAndIsFeatured(vendorId, true);
            long featuredItems = menuItemRepository.countByVendorIdAndIsFeatured(vendorId, true);
            long vegetarianItems = menuItemRepository.countByVendorIdAndIsVegetarian(vendorId, true);
            long veganItems = menuItemRepository.countByVendorIdAndIsVegan(vendorId, true);
            long glutenFreeItems = menuItemRepository.countByVendorIdAndIsGlutenFree(vendorId, true);
            long spicyItems = menuItemRepository.countByVendorIdAndIsSpicy(vendorId, true);

            List<MenuItemDto> recentItems = menuItemRepository.findTop5ByVendorIdOrderByCreatedAtDesc(vendorId)
                    .stream()
                    .map(this::convertToMenuItemDto)
                    .collect(Collectors.toList());

            List<MenuItemDto> featuredItemsList = menuItemRepository
                    .findTop5ByVendorIdAndIsFeaturedOrderByDisplayOrderAsc(vendorId, true)
                    .stream()
                    .map(this::convertToMenuItemDto)
                    .collect(Collectors.toList());

            MenuOverviewDto overview = MenuOverviewDto.builder()
                    .totalCategories((int) totalCategories)
                    .totalItems((int) totalItems)
                    .activeCategories((int) activeCategories)
                    .availableItems((int) activeItems)
                    .featuredItems((int) featuredItems)
                    .vegetarianItems((int) vegetarianItems)
                    .veganItems((int) veganItems)
                    .glutenFreeItems((int) glutenFreeItems)
                    .recentItems(recentItems)
                    .featuredItemsList(featuredItemsList)
                    .build();

            log.info("Successfully generated menu overview for vendor ID: {} - Categories: {}, Items: {}",
                    vendorId, totalCategories, totalItems);
            return overview;
        } catch (Exception e) {
            log.error("Error generating menu overview for vendor ID: {}", vendorId, e);
            throw e;
        }
    }

    @Override
    public Page<MenuItemDto> getFeaturedItems(Long vendorId, Pageable pageable) {
        log.info("Fetching featured items for vendor ID: {} with page: {}, size: {}",
                vendorId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<MenuItemEntity> items = menuItemRepository
                    .findTopByVendorIdAndIsFeaturedOrderByDisplayOrderAsc(vendorId, true, pageable);
            Page<MenuItemDto> result = items.map(this::convertToMenuItemDto);
            log.info("Successfully fetched {} featured items for vendor ID: {}", result.getTotalElements(), vendorId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching featured items for vendor ID: {}", vendorId, e);
            throw e;
        }
    }

    @Override
    public Page<MenuItemDto> getItemsByDietaryPreference(Long vendorId, String preference, Pageable pageable) {
        log.info("Fetching items by dietary preference for vendor ID: {} with preference: {}, page: {}, size: {}",
                vendorId, preference, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<MenuItemEntity> items;
            switch (preference.toUpperCase()) {
                case "VEGETARIAN":
                    items = menuItemRepository.findByVendorIdAndIsVegetarian(vendorId, true, pageable);
                    break;
                case "VEGAN":
                    items = menuItemRepository.findByVendorIdAndIsVegan(vendorId, true, pageable);
                    break;
                case "GLUTEN_FREE":
                    items = menuItemRepository.findByVendorIdAndIsGlutenFree(vendorId, true, pageable);
                    break;
                case "SPICY":
                    items = menuItemRepository.findByVendorIdAndIsSpicy(vendorId, true, pageable);
                    break;
                default:
                    log.warn("Invalid dietary preference: {}", preference);
                    throw new IllegalArgumentException("Invalid dietary preference: " + preference);
            }

            Page<MenuItemDto> result = items.map(this::convertToMenuItemDto);
            log.info("Successfully fetched {} items for dietary preference: {} for vendor ID: {}",
                    result.getTotalElements(), preference, vendorId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching items by dietary preference for vendor ID: {} with preference: {}",
                    vendorId, preference, e);
            throw e;
        }
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private MenuCategoryDto convertToCategoryDto(MenuCategoryEntity entity) {
        long itemCount = menuItemRepository.countByCategoryId(entity.getId());

        return MenuCategoryDto.builder()
                .id(entity.getId())
                .vendorId(entity.getVendorId())
                .name(entity.getName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .isActive(entity.getIsActive())
                .isFeatured(entity.getIsFeatured())
                .imageUrl(entity.getImageUrl())
                .itemCount((int) itemCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private MenuItemDto convertToMenuItemDto(MenuItemEntity entity) {
        // Get category name
        String categoryName = menuCategoryRepository.findById(entity.getCategoryId())
                .map(MenuCategoryEntity::getName)
                .orElse("Unknown Category");

        // Get primary image URL
        String primaryImageUrl = menuItemImageRepository.findByMenuItemIdAndIsPrimary(entity.getId(), true)
                .stream()
                .findFirst()
                .map(MenuItemImageEntity::getImageUrl)
                .orElse(null);

        // Calculate discount information
        boolean hasDiscount = entity.getOriginalPrice() != null &&
                entity.getOriginalPrice().compareTo(entity.getPrice()) > 0;
        BigDecimal discountPercentage = null;
        if (hasDiscount && entity.getOriginalPrice() != null
                && entity.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
            discountPercentage = entity.getOriginalPrice()
                    .subtract(entity.getPrice())
                    .divide(entity.getOriginalPrice(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return MenuItemDto.builder()
                .id(entity.getId())
                .vendorId(entity.getVendorId())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryName)
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .originalPrice(entity.getOriginalPrice())
                .isAvailable(entity.getIsAvailable())
                .isFeatured(entity.getIsFeatured())
                .isVegetarian(entity.getIsVegetarian())
                .isVegan(entity.getIsVegan())
                .isGlutenFree(entity.getIsGlutenFree())
                .isSpicy(entity.getIsSpicy())
                .calories(entity.getCalories())
                .proteinGrams(entity.getProteinGrams())
                .carbsGrams(entity.getCarbsGrams())
                .fatGrams(entity.getFatGrams())
                .allergens(entity.getAllergens())
                .ingredients(entity.getIngredients())
                .cookingInstructions(entity.getCookingInstructions())
                .preparationTimeMinutes(entity.getPreparationTimeMinutes())
                .displayOrder(entity.getDisplayOrder())
                .primaryImageUrl(primaryImageUrl)
                .hasDiscount(hasDiscount)
                .discountPercentage(discountPercentage)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private MenuItemImageDto convertToMenuItemImageDto(MenuItemImageEntity entity) {
        return MenuItemImageDto.builder()
                .id(entity.getId())
                .menuItemId(entity.getMenuItemId())
                .imageUrl(entity.getImageUrl())
                .imageType(entity.getImageType())
                .fileName(entity.getFileName())
                .fileSizeBytes(entity.getFileSizeBytes())
                .widthPixels(entity.getWidthPixels())
                .heightPixels(entity.getHeightPixels())
                .isPrimary(entity.getIsPrimary())
                .displayOrder(entity.getDisplayOrder())
                .altText(entity.getAltText())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private MenuItemAvailabilityDto convertToMenuItemAvailabilityDto(MenuItemAvailabilityEntity entity) {
        return MenuItemAvailabilityDto.builder()
                .id(entity.getId())
                .menuItemId(entity.getMenuItemId())
                .dayOfWeek(entity.getDayOfWeek())
                .isAvailable(entity.getIsAvailable())
                .availableFrom(entity.getAvailableFrom())
                .availableUntil(entity.getAvailableUntil())
                .maxQuantityPerDay(entity.getMaxQuantityPerDay())
                .currentQuantityAvailable(entity.getCurrentQuantityAvailable())
                .isSpecialOffer(entity.getIsSpecialOffer())
                .specialOfferPrice(entity.getSpecialOfferPrice())
                .specialOfferDescription(entity.getSpecialOfferDescription())
                .specialOfferValidFrom(entity.getSpecialOfferValidFrom())
                .specialOfferValidUntil(entity.getSpecialOfferValidUntil())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}