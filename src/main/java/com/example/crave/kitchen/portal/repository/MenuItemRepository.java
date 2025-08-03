package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.MenuItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

        // Basic CRUD operations
        List<MenuItemEntity> findByVendorIdAndIsAvailableOrderByDisplayOrderAsc(Long vendorId, Boolean isAvailable);

        List<MenuItemEntity> findByVendorIdAndCategoryIdAndIsAvailableOrderByDisplayOrderAsc(Long vendorId,
                        Long categoryId,
                        Boolean isAvailable);

        List<MenuItemEntity> findByVendorIdAndIsFeaturedAndIsAvailableOrderByDisplayOrderAsc(Long vendorId,
                        Boolean isFeatured, Boolean isAvailable);

        Page<MenuItemEntity> findByVendorIdAndIsAvailable(Long vendorId, Boolean isAvailable, Pageable pageable);

        // Advanced filtering with pagination
        @Query("SELECT m FROM MenuItemEntity m WHERE m.vendorId = :vendorId AND m.isAvailable = true AND " +
                        "(:categoryId IS NULL OR m.categoryId = :categoryId) AND " +
                        "(:isVegetarian IS NULL OR m.isVegetarian = :isVegetarian) AND " +
                        "(:isVegan IS NULL OR m.isVegan = :isVegan) AND " +
                        "(:isGlutenFree IS NULL OR m.isGlutenFree = :isGlutenFree) AND " +
                        "(:isSpicy IS NULL OR m.isSpicy = :isSpicy) AND " +
                        "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
                        "(:searchTerm IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                        "ORDER BY m.displayOrder ASC")
        Page<MenuItemEntity> findMenuItemsWithFilters(
                        @Param("vendorId") Long vendorId,
                        @Param("categoryId") Long categoryId,
                        @Param("isVegetarian") Boolean isVegetarian,
                        @Param("isVegan") Boolean isVegan,
                        @Param("isGlutenFree") Boolean isGlutenFree,
                        @Param("isSpicy") Boolean isSpicy,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("searchTerm") String searchTerm,
                        Pageable pageable);

        // Search functionality
        @Query("SELECT m FROM MenuItemEntity m WHERE m.vendorId = :vendorId AND " +
                        "(LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.ingredients) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                        "ORDER BY m.displayOrder ASC")
        Page<MenuItemEntity> findByVendorIdAndSearchTerm(@Param("vendorId") Long vendorId,
                        @Param("searchTerm") String searchTerm,
                        Pageable pageable);

        // Validation methods
        Optional<MenuItemEntity> findByVendorIdAndNameAndIsAvailable(Long vendorId, String name, Boolean isAvailable);

        boolean existsByVendorIdAndNameAndIdNot(Long vendorId, String name, Long id);

        boolean existsByVendorIdAndName(Long vendorId, String name);

        // Count methods
        @Query("SELECT COUNT(m) FROM MenuItemEntity m WHERE m.vendorId = :vendorId AND m.isAvailable = true")
        long countAvailableItemsByVendorId(@Param("vendorId") Long vendorId);

        @Query("SELECT COUNT(m) FROM MenuItemEntity m WHERE m.vendorId = :vendorId AND m.categoryId = :categoryId AND m.isAvailable = true")
        long countAvailableItemsByVendorIdAndCategoryId(@Param("vendorId") Long vendorId,
                        @Param("categoryId") Long categoryId);

        long countByVendorId(Long vendorId);

        long countByVendorIdAndIsAvailable(Long vendorId, Boolean isAvailable);

        long countByVendorIdAndIsFeatured(Long vendorId, Boolean isFeatured);

        long countByVendorIdAndIsVegetarian(Long vendorId, Boolean isVegetarian);

        long countByVendorIdAndIsVegan(Long vendorId, Boolean isVegan);

        long countByVendorIdAndIsGlutenFree(Long vendorId, Boolean isGlutenFree);

        long countByVendorIdAndIsSpicy(Long vendorId, Boolean isSpicy);

        long countByCategoryId(Long categoryId);

        // Dietary preference methods
        List<MenuItemEntity> findByVendorIdAndIsAvailableAndIsVegetarianOrderByDisplayOrderAsc(Long vendorId,
                        Boolean isAvailable, Boolean isVegetarian);

        List<MenuItemEntity> findByVendorIdAndIsAvailableAndIsVeganOrderByDisplayOrderAsc(Long vendorId,
                        Boolean isAvailable, Boolean isVegan);

        List<MenuItemEntity> findByVendorIdAndIsAvailableAndIsGlutenFreeOrderByDisplayOrderAsc(Long vendorId,
                        Boolean isAvailable, Boolean isGlutenFree);

        // Pagination support for dietary preferences
        Page<MenuItemEntity> findByVendorIdAndIsVegetarian(Long vendorId, Boolean isVegetarian, Pageable pageable);

        Page<MenuItemEntity> findByVendorIdAndIsVegan(Long vendorId, Boolean isVegan, Pageable pageable);

        Page<MenuItemEntity> findByVendorIdAndIsGlutenFree(Long vendorId, Boolean isGlutenFree, Pageable pageable);

        Page<MenuItemEntity> findByVendorIdAndIsSpicy(Long vendorId, Boolean isSpicy, Pageable pageable);

        // Featured items with pagination
        List<MenuItemEntity> findTop5ByVendorIdOrderByCreatedAtDesc(Long vendorId);

        List<MenuItemEntity> findTop5ByVendorIdAndIsFeaturedOrderByDisplayOrderAsc(Long vendorId, Boolean isFeatured);

        @Query("SELECT m FROM MenuItemEntity m WHERE m.vendorId = :vendorId AND m.isFeatured = :isFeatured ORDER BY m.displayOrder ASC")
        Page<MenuItemEntity> findTopByVendorIdAndIsFeaturedOrderByDisplayOrderAsc(@Param("vendorId") Long vendorId,
                        @Param("isFeatured") Boolean isFeatured, Pageable pageable);
}