package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.*;
import com.example.crave.kitchen.portal.entity.VendorsEntity;
import com.example.crave.kitchen.portal.repository.VendorRepository;
import com.example.crave.kitchen.portal.service.JwtTokenService;
import com.example.crave.kitchen.portal.service.MenuService;
import com.example.crave.kitchen.portal.util.TokenDataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MenuController {

        private final MenuService menuService;

        @Autowired
        private VendorRepository vendorRepository;

        @Autowired
        private JwtTokenService jwtTokenService;

        @Autowired
        private TokenDataExtractor tokenDataExtractor;

        // =====================================================
        // UTILITY METHODS
        // =====================================================

        /**
         * Debug endpoint to test token extraction
         */
        @GetMapping("/debug/token")
        public ResponseEntity<ApiResponseDto<Map<String, Object>>> debugToken() {
                log.info("GET /api/menu/debug/token - Debugging token extraction");

                try {
                        // Log current authentication
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        log.debug("Current authentication: {}", authentication);

                        // Try to extract vendor ID
                        Long vendorId = tokenDataExtractor.getCurrentVendorId();
                        String email = tokenDataExtractor.getCurrentUserEmail();
                        Map<String, Object> allTokenData = tokenDataExtractor.getAllTokenData();

                        Map<String, Object> debugInfo = Map.of(
                                        "authenticationName",
                                        authentication != null ? authentication.getName() : "null",
                                        "isAuthenticated",
                                        authentication != null ? authentication.isAuthenticated() : false,
                                        "authorities",
                                        authentication != null ? authentication.getAuthorities() : "null",
                                        "vendorId", vendorId,
                                        "email", email,
                                        "allTokenData", allTokenData);

                        log.info("Debug info: {}", debugInfo);
                        return ResponseEntity.ok(ApiResponseDto.<Map<String, Object>>builder()
                                        .success(true)
                                        .message("Token debug information")
                                        .data(debugInfo)
                                        .build());
                } catch (Exception e) {
                        log.error("Error in token debug: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Map<String, Object>>builder()
                                                        .success(false)
                                                        .message("Error debugging token: " + e.getMessage())
                                                        .build());
                }
        }

        /**
         * Simple test endpoint to verify vendor ID extraction
         */
        @GetMapping("/test-vendor-id")
        public ResponseEntity<ApiResponseDto<Map<String, Object>>> testVendorId() {
                log.info("GET /api/menu/test-vendor-id - Testing vendor ID extraction");

                try {
                        // Test token extraction directly
                        String token = extractTokenFromRequest();
                        log.info("Token extracted: {}", token != null ? "YES" : "NO");

                        // Test vendor ID extraction
                        Long vendorId = getCurrentVendorId();

                        // Get authentication info
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        String authName = auth != null ? auth.getName() : "null";
                        boolean isAuthenticated = auth != null && auth.isAuthenticated();

                        Map<String, Object> result = Map.of(
                                        "tokenFound", token != null,
                                        "tokenLength", token != null ? token.length() : 0,
                                        "vendorId", vendorId,
                                        "authName", authName,
                                        "isAuthenticated", isAuthenticated,
                                        "success", vendorId != null,
                                        "message", vendorId != null ? "Vendor ID extracted successfully"
                                                        : "Failed to extract vendor ID");

                        log.info("Vendor ID test result: {}", result);
                        return ResponseEntity.ok(ApiResponseDto.<Map<String, Object>>builder()
                                        .success(true)
                                        .message("Vendor ID test completed")
                                        .data(result)
                                        .build());
                } catch (Exception e) {
                        log.error("Error testing vendor ID: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Map<String, Object>>builder()
                                                        .success(false)
                                                        .message("Error testing vendor ID: " + e.getMessage())
                                                        .build());
                }
        }

        /**
         * Extract token from request for debugging
         */
        private String extractTokenFromRequest() {
                try {
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                                        .currentRequestAttributes())
                                        .getRequest();
                        String authHeader = request.getHeader("Authorization");
                        log.info("Authorization header found: {}", authHeader != null);
                        if (authHeader != null) {
                                log.info("Authorization header starts with Bearer: {}",
                                                authHeader.startsWith("Bearer "));
                        }

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                return authHeader.substring(7);
                        }
                } catch (Exception e) {
                        log.error("Failed to extract token from request: {}", e.getMessage(), e);
                }
                return null;
        }

        /**
         * Extract vendor ID from the JWT token
         */
        private Long getCurrentVendorId() {
                // Use the utility class to extract vendor ID from token
                Long vendorId = tokenDataExtractor.getCurrentVendorId();
                if (vendorId != null) {
                        log.debug("Extracted vendor ID from token: {}", vendorId);
                        return vendorId;
                }

                // Fallback: try to get from database using email
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                        String email = authentication.getName();
                        if ("anonymousUser".equals(email)) {
                                log.error("User is anonymous - JWT authentication may have failed");
                                throw new RuntimeException("User is not properly authenticated");
                        }

                        log.debug("Using fallback method - getting vendor from database for email: {}", email);
                        try {
                                VendorsEntity vendor = vendorRepository.findByEmail(email)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Vendor not found for email: " + email));
                                return vendor.getId();
                        } catch (Exception e) {
                                log.error("Failed to get vendor ID: {}", e.getMessage(), e);
                                throw new RuntimeException("Failed to get vendor information", e);
                        }
                }

                log.error("User not authenticated - authentication is null or not authenticated");
                throw new RuntimeException("User not authenticated");
        }

        // =====================================================
        // MENU CATEGORIES
        // =====================================================

        @GetMapping("/categories")
        public ResponseEntity<ApiResponseDto<Page<MenuCategoryDto>>> getAllCategories(
                        @RequestParam @NotNull Long vendorId,
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(required = false) Boolean isFeatured,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                        @RequestParam(defaultValue = "displayOrder") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir) {

                log.info("GET /api/menu/categories - vendorId: {}, isActive: {}, isFeatured: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                                vendorId, isActive, isFeatured, page, size, sortBy, sortDir);

                try {
                        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
                        Pageable pageable = PageRequest.of(page, size, sort);

                        Page<MenuCategoryDto> categories = menuService.getAllCategories(vendorId, isActive, isFeatured,
                                        pageable);

                        log.info("Successfully retrieved {} categories for vendorId: {}", categories.getTotalElements(),
                                        vendorId);

                        return ResponseEntity.ok(ApiResponseDto.<Page<MenuCategoryDto>>builder()
                                        .success(true)
                                        .message("Categories retrieved successfully")
                                        .data(categories)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving categories for vendorId: {}", vendorId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Page<MenuCategoryDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving categories: " + e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/categories/{id}")
        public ResponseEntity<ApiResponseDto<MenuCategoryDto>> getCategoryById(@PathVariable @NotNull Long id) {
                log.info("GET /api/menu/categories/{}", id);

                try {
                        var category = menuService.getCategoryById(id);
                        if (category.isPresent()) {
                                log.info("Successfully retrieved category with ID: {}", id);
                                return ResponseEntity.ok(ApiResponseDto.<MenuCategoryDto>builder()
                                                .success(true)
                                                .message("Category retrieved successfully")
                                                .data(category.get())
                                                .build());
                        } else {
                                log.warn("Category not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                                .success(false)
                                                                .message("Category not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error retrieving category with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                        .success(false)
                                                        .message("Error retrieving category: " + e.getMessage())
                                                        .build());
                }
        }

        @PostMapping("/categories")
        public ResponseEntity<ApiResponseDto<MenuCategoryDto>> createCategory(
                        @Valid @RequestBody CreateMenuCategoryRequestDto requestDto) {
                log.info("POST /api/menu/categories - Creating category: {}", requestDto.getName());

                try {
                        Long vendorId = getCurrentVendorId();

                        MenuCategoryDto createdCategory = menuService.createCategory(vendorId, requestDto);

                        log.info("Successfully created category with ID: {} and name: {}",
                                        createdCategory.getId(), createdCategory.getName());

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                        .success(true)
                                                        .message("Category created successfully")
                                                        .data(createdCategory)
                                                        .build());
                } catch (Exception e) {
                        log.error("Error creating category: {}", requestDto.getName(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                        .success(false)
                                                        .message("Error creating category: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/categories/{id}")
        public ResponseEntity<ApiResponseDto<MenuCategoryDto>> updateCategory(
                        @PathVariable @NotNull Long id,
                        @Valid @RequestBody CreateMenuCategoryRequestDto requestDto) {
                log.info("PUT /api/menu/categories/{} - Updating category: {}", id, requestDto.getName());

                try {
                        Long vendorId = getCurrentVendorId();

                        var updatedCategory = menuService.updateCategory(vendorId, id, requestDto);
                        if (updatedCategory.isPresent()) {
                                log.info("Successfully updated category with ID: {} and name: {}",
                                                updatedCategory.get().getId(), updatedCategory.get().getName());
                                return ResponseEntity.ok(ApiResponseDto.<MenuCategoryDto>builder()
                                                .success(true)
                                                .message("Category updated successfully")
                                                .data(updatedCategory.get())
                                                .build());
                        } else {
                                log.warn("Category not found for update with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                                .success(false)
                                                                .message("Category not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error updating category with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuCategoryDto>builder()
                                                        .success(false)
                                                        .message("Error updating category: " + e.getMessage())
                                                        .build());
                }
        }

        @DeleteMapping("/categories/{id}")
        public ResponseEntity<ApiResponseDto<Void>> deleteCategory(@PathVariable @NotNull Long id) {
                log.info("DELETE /api/menu/categories/{}", id);

                try {
                        boolean deleted = menuService.deleteCategory(id);
                        if (deleted) {
                                log.info("Successfully deleted category with ID: {}", id);
                                return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                                                .success(true)
                                                .message("Category deleted successfully")
                                                .build());
                        } else {
                                log.warn("Category not found for deletion with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<Void>builder()
                                                                .success(false)
                                                                .message("Category not found or cannot be deleted")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error deleting category with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Void>builder()
                                                        .success(false)
                                                        .message("Error deleting category: " + e.getMessage())
                                                        .build());
                }
        }

        // =====================================================
        // MENU ITEMS
        // =====================================================

        @GetMapping("/items")
        public ResponseEntity<ApiResponseDto<Page<MenuItemDto>>> getAllMenuItems(
                        @RequestParam @NotNull Long vendorId,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) Boolean isAvailable,
                        @RequestParam(required = false) Boolean isFeatured,
                        @RequestParam(required = false) Boolean isVegetarian,
                        @RequestParam(required = false) Boolean isVegan,
                        @RequestParam(required = false) Boolean isGlutenFree,
                        @RequestParam(required = false) Boolean isSpicy,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) String searchTerm,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                        @RequestParam(defaultValue = "displayOrder") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir) {

                log.info("GET /api/menu/items - vendorId: {}, categoryId: {}, isAvailable: {}, isFeatured: {}, " +
                                "isVegetarian: {}, isVegan: {}, isGlutenFree: {}, isSpicy: {}, minPrice: {}, maxPrice: {}, "
                                +
                                "searchTerm: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                                vendorId, categoryId, isAvailable, isFeatured, isVegetarian, isVegan, isGlutenFree,
                                isSpicy, minPrice, maxPrice, searchTerm, page, size, sortBy, sortDir);

                try {
                        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
                        Pageable pageable = PageRequest.of(page, size, sort);

                        Page<MenuItemDto> items = menuService.getAllMenuItems(vendorId, categoryId, isAvailable,
                                        isFeatured, isVegetarian, isVegan, isGlutenFree, isSpicy, minPrice, maxPrice,
                                        searchTerm, pageable);

                        log.info("Successfully retrieved {} menu items for vendorId: {}", items.getTotalElements(),
                                        vendorId);

                        return ResponseEntity.ok(ApiResponseDto.<Page<MenuItemDto>>builder()
                                        .success(true)
                                        .message("Menu items retrieved successfully")
                                        .data(items)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving menu items for vendorId: {}", vendorId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Page<MenuItemDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving menu items: " + e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/items/{id}")
        public ResponseEntity<ApiResponseDto<MenuItemDto>> getMenuItemById(@PathVariable @NotNull Long id) {
                log.info("GET /api/menu/items/{}", id);

                try {
                        var item = menuService.getMenuItemById(id);
                        if (item.isPresent()) {
                                log.info("Successfully retrieved menu item with ID: {}", id);
                                return ResponseEntity.ok(ApiResponseDto.<MenuItemDto>builder()
                                                .success(true)
                                                .message("Menu item retrieved successfully")
                                                .data(item.get())
                                                .build());
                        } else {
                                log.warn("Menu item not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<MenuItemDto>builder()
                                                                .success(false)
                                                                .message("Menu item not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error retrieving menu item with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuItemDto>builder()
                                                        .success(false)
                                                        .message("Error retrieving menu item: " + e.getMessage())
                                                        .build());
                }
        }

        @PostMapping("/items")
        public ResponseEntity<ApiResponseDto<MenuItemDto>> createMenuItem(
                        @Valid @RequestBody CreateMenuItemRequestDto requestDto) {
                log.info("POST /api/menu/items - Creating menu item: {}", requestDto.getName());

                try {
                        Long vendorId = getCurrentVendorId();

                        MenuItemDto createdItem = menuService.createMenuItem(vendorId, requestDto);

                        log.info("Successfully created menu item with ID: {} and name: {}",
                                        createdItem.getId(), createdItem.getName());

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponseDto.<MenuItemDto>builder()
                                                        .success(true)
                                                        .message("Menu item created successfully")
                                                        .data(createdItem)
                                                        .build());
                } catch (Exception e) {
                        log.error("Error creating menu item: {}", requestDto.getName(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuItemDto>builder()
                                                        .success(false)
                                                        .message("Error creating menu item: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/items/{id}")
        public ResponseEntity<ApiResponseDto<MenuItemDto>> updateMenuItem(
                        @PathVariable @NotNull Long id,
                        @Valid @RequestBody CreateMenuItemRequestDto requestDto) {
                log.info("PUT /api/menu/items/{} - Updating menu item: {}", id, requestDto.getName());

                try {
                        Long vendorId = getCurrentVendorId();

                        var updatedItem = menuService.updateMenuItem(vendorId, id, requestDto);
                        if (updatedItem.isPresent()) {
                                log.info("Successfully updated menu item with ID: {} and name: {}",
                                                updatedItem.get().getId(), updatedItem.get().getName());
                                return ResponseEntity.ok(ApiResponseDto.<MenuItemDto>builder()
                                                .success(true)
                                                .message("Menu item updated successfully")
                                                .data(updatedItem.get())
                                                .build());
                        } else {
                                log.warn("Menu item not found for update with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<MenuItemDto>builder()
                                                                .success(false)
                                                                .message("Menu item not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error updating menu item with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuItemDto>builder()
                                                        .success(false)
                                                        .message("Error updating menu item: " + e.getMessage())
                                                        .build());
                }
        }

        @DeleteMapping("/items/{id}")
        public ResponseEntity<ApiResponseDto<Void>> deleteMenuItem(@PathVariable @NotNull Long id) {
                log.info("DELETE /api/menu/items/{}", id);

                try {
                        boolean deleted = menuService.deleteMenuItem(id);
                        if (deleted) {
                                log.info("Successfully deleted menu item with ID: {}", id);
                                return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                                                .success(true)
                                                .message("Menu item deleted successfully")
                                                .build());
                        } else {
                                log.warn("Menu item not found for deletion with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<Void>builder()
                                                                .success(false)
                                                                .message("Menu item not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error deleting menu item with ID: {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Void>builder()
                                                        .success(false)
                                                        .message("Error deleting menu item: " + e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/items/search")
        public ResponseEntity<ApiResponseDto<Page<MenuItemDto>>> searchMenuItems(
                        @RequestParam @NotNull Long vendorId,
                        @RequestParam @NotBlank String searchTerm,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

                log.info("GET /api/menu/items/search - vendorId: {}, searchTerm: '{}', page: {}, size: {}",
                                vendorId, searchTerm, page, size);

                try {
                        Pageable pageable = PageRequest.of(page, size);
                        Page<MenuItemDto> items = menuService.searchMenuItems(vendorId, searchTerm, pageable);

                        log.info("Search completed successfully. Found {} items for search term: '{}'",
                                        items.getTotalElements(), searchTerm);

                        return ResponseEntity.ok(ApiResponseDto.<Page<MenuItemDto>>builder()
                                        .success(true)
                                        .message("Search completed successfully")
                                        .data(items)
                                        .build());
                } catch (Exception e) {
                        log.error("Error searching menu items for vendorId: {} with search term: '{}'", vendorId,
                                        searchTerm, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Page<MenuItemDto>>builder()
                                                        .success(false)
                                                        .message("Error searching menu items: " + e.getMessage())
                                                        .build());
                }
        }

        // =====================================================
        // MENU ITEM IMAGES
        // =====================================================

        @GetMapping("/items/{menuItemId}/images")
        public ResponseEntity<ApiResponseDto<List<MenuItemImageDto>>> getImagesForMenuItem(
                        @PathVariable @NotNull Long menuItemId) {
                log.info("GET /api/menu/items/{}/images", menuItemId);

                try {
                        List<MenuItemImageDto> images = menuService.getImagesForMenuItem(menuItemId);

                        log.info("Successfully retrieved {} images for menu item ID: {}", images.size(), menuItemId);

                        return ResponseEntity.ok(ApiResponseDto.<List<MenuItemImageDto>>builder()
                                        .success(true)
                                        .message("Images retrieved successfully")
                                        .data(images)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving images for menu item ID: {}", menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<List<MenuItemImageDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving images: " + e.getMessage())
                                                        .build());
                }
        }

        @PostMapping(value = "/items/{menuItemId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponseDto<MenuItemImageDto>> uploadImage(
                        @PathVariable @NotNull Long menuItemId,
                        @RequestParam("file") MultipartFile file,
                        @RequestParam(required = false) String imageType,
                        @RequestParam(defaultValue = "false") boolean isPrimary,
                        @RequestParam(defaultValue = "1") int displayOrder,
                        @RequestParam(required = false) String altText) {

                log.info("POST /api/menu/items/{}/images - Uploading image: {}, size: {} bytes, isPrimary: {}",
                                menuItemId, file.getOriginalFilename(), file.getSize(), isPrimary);

                try {
                        // Create image DTO from multipart file
                        MenuItemImageDto imageDto = MenuItemImageDto.builder()
                                        .menuItemId(menuItemId)
                                        .imageUrl("uploaded-url-" + System.currentTimeMillis()) // This would be set by
                                                                                                // file upload service
                                        .imageType(imageType != null ? imageType : "JPEG")
                                        .fileName(file.getOriginalFilename())
                                        .fileSizeBytes(file.getSize())
                                        .isPrimary(isPrimary)
                                        .displayOrder(displayOrder)
                                        .altText(altText)
                                        .build();

                        MenuItemImageDto uploadedImage = menuService.uploadImage(menuItemId, imageDto);

                        log.info("Successfully uploaded image with ID: {} for menu item ID: {}",
                                        uploadedImage.getId(), menuItemId);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponseDto.<MenuItemImageDto>builder()
                                                        .success(true)
                                                        .message("Image uploaded successfully")
                                                        .data(uploadedImage)
                                                        .build());
                } catch (Exception e) {
                        log.error("Error uploading image for menu item ID: {}", menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuItemImageDto>builder()
                                                        .success(false)
                                                        .message("Error uploading image: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/items/{menuItemId}/images/{imageId}/primary")
        public ResponseEntity<ApiResponseDto<Void>> setPrimaryImage(
                        @PathVariable @NotNull Long menuItemId,
                        @PathVariable @NotNull Long imageId) {

                log.info("PUT /api/menu/items/{}/images/{}/primary", menuItemId, imageId);

                try {
                        boolean success = menuService.setPrimaryImage(menuItemId, imageId);
                        if (success) {
                                log.info("Successfully set primary image for menu item ID: {} with image ID: {}",
                                                menuItemId, imageId);
                                return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                                                .success(true)
                                                .message("Primary image set successfully")
                                                .build());
                        } else {
                                log.warn("Failed to set primary image for menu item ID: {} with image ID: {}",
                                                menuItemId, imageId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<Void>builder()
                                                                .success(false)
                                                                .message("Image not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error setting primary image for menu item ID: {} with image ID: {}", menuItemId,
                                        imageId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Void>builder()
                                                        .success(false)
                                                        .message("Error setting primary image: " + e.getMessage())
                                                        .build());
                }
        }

        @DeleteMapping("/items/{menuItemId}/images/{imageId}")
        public ResponseEntity<ApiResponseDto<Void>> deleteImage(
                        @PathVariable @NotNull Long menuItemId,
                        @PathVariable @NotNull Long imageId) {

                log.info("DELETE /api/menu/items/{}/images/{}", menuItemId, imageId);

                try {
                        boolean deleted = menuService.deleteImage(menuItemId, imageId);
                        if (deleted) {
                                log.info("Successfully deleted image with ID: {} for menu item ID: {}", imageId,
                                                menuItemId);
                                return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                                                .success(true)
                                                .message("Image deleted successfully")
                                                .build());
                        } else {
                                log.warn("Image not found for deletion with ID: {} for menu item ID: {}", imageId,
                                                menuItemId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ApiResponseDto.<Void>builder()
                                                                .success(false)
                                                                .message("Image not found")
                                                                .build());
                        }
                } catch (Exception e) {
                        log.error("Error deleting image with ID: {} for menu item ID: {}", imageId, menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Void>builder()
                                                        .success(false)
                                                        .message("Error deleting image: " + e.getMessage())
                                                        .build());
                }
        }

        // =====================================================
        // MENU ITEM AVAILABILITY
        // =====================================================

        @GetMapping("/items/{menuItemId}/availability")
        public ResponseEntity<ApiResponseDto<List<MenuItemAvailabilityDto>>> getAvailabilityForMenuItem(
                        @PathVariable @NotNull Long menuItemId) {
                log.info("GET /api/menu/items/{}/availability", menuItemId);

                try {
                        List<MenuItemAvailabilityDto> availability = menuService.getAvailabilityForMenuItem(menuItemId);

                        log.info("Successfully retrieved {} availability records for menu item ID: {}",
                                        availability.size(), menuItemId);

                        return ResponseEntity.ok(ApiResponseDto.<List<MenuItemAvailabilityDto>>builder()
                                        .success(true)
                                        .message("Availability retrieved successfully")
                                        .data(availability)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving availability for menu item ID: {}", menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<List<MenuItemAvailabilityDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving availability: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/items/{menuItemId}/availability")
        public ResponseEntity<ApiResponseDto<List<MenuItemAvailabilityDto>>> updateAvailability(
                        @PathVariable @NotNull Long menuItemId,
                        @Valid @RequestBody List<MenuItemAvailabilityDto> availabilityDtos) {

                log.info("PUT /api/menu/items/{}/availability - Updating {} availability records",
                                menuItemId, availabilityDtos.size());

                try {
                        List<MenuItemAvailabilityDto> updatedAvailability = menuService.updateAvailability(menuItemId,
                                        availabilityDtos);

                        log.info("Successfully updated availability for menu item ID: {} with {} records",
                                        menuItemId, updatedAvailability.size());

                        return ResponseEntity.ok(ApiResponseDto.<List<MenuItemAvailabilityDto>>builder()
                                        .success(true)
                                        .message("Availability updated successfully")
                                        .data(updatedAvailability)
                                        .build());
                } catch (Exception e) {
                        log.error("Error updating availability for menu item ID: {}", menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<List<MenuItemAvailabilityDto>>builder()
                                                        .success(false)
                                                        .message("Error updating availability: " + e.getMessage())
                                                        .build());
                }
        }

        @PostMapping("/items/{menuItemId}/special-offers")
        public ResponseEntity<ApiResponseDto<MenuItemAvailabilityDto>> createSpecialOffer(
                        @PathVariable @NotNull Long menuItemId,
                        @Valid @RequestBody MenuItemAvailabilityDto specialOfferDto) {

                log.info("POST /api/menu/items/{}/special-offers - Creating special offer for day: {}",
                                menuItemId, specialOfferDto.getDayOfWeek());

                try {
                        MenuItemAvailabilityDto createdSpecialOffer = menuService.createSpecialOffer(menuItemId,
                                        specialOfferDto);

                        log.info("Successfully created special offer with ID: {} for menu item ID: {}",
                                        createdSpecialOffer.getId(), menuItemId);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponseDto.<MenuItemAvailabilityDto>builder()
                                                        .success(true)
                                                        .message("Special offer created successfully")
                                                        .data(createdSpecialOffer)
                                                        .build());
                } catch (Exception e) {
                        log.error("Error creating special offer for menu item ID: {}", menuItemId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuItemAvailabilityDto>builder()
                                                        .success(false)
                                                        .message("Error creating special offer: " + e.getMessage())
                                                        .build());
                }
        }

        // =====================================================
        // MENU OVERVIEW & ANALYTICS
        // =====================================================

        @GetMapping("/overview")
        public ResponseEntity<ApiResponseDto<MenuOverviewDto>> getMenuOverview(@RequestParam @NotNull Long vendorId) {
                log.info("GET /api/menu/overview - vendorId: {}", vendorId);

                try {
                        MenuOverviewDto overview = menuService.getMenuOverview(vendorId);

                        log.info("Successfully generated menu overview for vendor ID: {} - Categories: {}, Items: {}",
                                        vendorId, overview.getTotalCategories(), overview.getTotalItems());

                        return ResponseEntity.ok(ApiResponseDto.<MenuOverviewDto>builder()
                                        .success(true)
                                        .message("Menu overview retrieved successfully")
                                        .data(overview)
                                        .build());
                } catch (Exception e) {
                        log.error("Error generating menu overview for vendor ID: {}", vendorId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<MenuOverviewDto>builder()
                                                        .success(false)
                                                        .message("Error generating menu overview: " + e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/items/featured")

        public ResponseEntity<ApiResponseDto<Page<MenuItemDto>>> getFeaturedItems(
                        @RequestParam @NotNull Long vendorId,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

                log.info("GET /api/menu/items/featured - vendorId: {}, page: {}, size: {}", vendorId, page, size);

                try {
                        Pageable pageable = PageRequest.of(page, size);
                        Page<MenuItemDto> featuredItems = menuService.getFeaturedItems(vendorId, pageable);

                        log.info("Successfully retrieved {} featured items for vendor ID: {}",
                                        featuredItems.getTotalElements(),
                                        vendorId);

                        return ResponseEntity.ok(ApiResponseDto.<Page<MenuItemDto>>builder()
                                        .success(true)
                                        .message("Featured items retrieved successfully")
                                        .data(featuredItems)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving featured items for vendor ID: {}", vendorId, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Page<MenuItemDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving featured items: " + e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/items/dietary/{preference}")

        public ResponseEntity<ApiResponseDto<Page<MenuItemDto>>> getItemsByDietaryPreference(
                        @RequestParam @NotNull Long vendorId,
                        @PathVariable @NotBlank String preference,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

                log.info("GET /api/menu/items/dietary/{} - vendorId: {}, preference: {}, page: {}, size: {}",
                                preference, vendorId, preference, page, size);

                try {
                        Pageable pageable = PageRequest.of(page, size);
                        Page<MenuItemDto> items = menuService.getItemsByDietaryPreference(vendorId, preference,
                                        pageable);

                        log.info("Successfully retrieved {} items for dietary preference: {} for vendor ID: {}",
                                        items.getTotalElements(), preference, vendorId);

                        return ResponseEntity.ok(ApiResponseDto.<Page<MenuItemDto>>builder()
                                        .success(true)
                                        .message("Items retrieved successfully")
                                        .data(items)
                                        .build());
                } catch (Exception e) {
                        log.error("Error retrieving items by dietary preference for vendor ID: {} with preference: {}",
                                        vendorId, preference, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponseDto.<Page<MenuItemDto>>builder()
                                                        .success(false)
                                                        .message("Error retrieving items: " + e.getMessage())
                                                        .build());
                }
        }
}