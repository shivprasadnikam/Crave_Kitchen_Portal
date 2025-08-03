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

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = :isAvailable "
                        +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = :isAvailable", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndIsAvailableNative(@Param("vendorId") Long vendorId,
                        @Param("isAvailable") Boolean isAvailable,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = :isAvailable", nativeQuery = true)
        long countByVendorIdAndIsAvailableNative(@Param("vendorId") Long vendorId,
                        @Param("isAvailable") Boolean isAvailable);

        // Advanced filtering with pagination using native SQL for Oracle compatibility
        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = 1 AND " +
                        "(:categoryId IS NULL OR m.category_id = :categoryId) AND " +
                        "(:isVegetarian IS NULL OR m.is_vegetarian = :isVegetarian) AND " +
                        "(:isVegan IS NULL OR m.is_vegan = :isVegan) AND " +
                        "(:isGlutenFree IS NULL OR m.is_gluten_free = :isGlutenFree) AND " +
                        "(:isSpicy IS NULL OR m.is_spicy = :isSpicy) AND " +
                        "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
                        "(:searchTerm IS NULL OR LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%')) " +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = 1 AND "
                                        +
                                        "(:categoryId IS NULL OR m.category_id = :categoryId) AND " +
                                        "(:isVegetarian IS NULL OR m.is_vegetarian = :isVegetarian) AND " +
                                        "(:isVegan IS NULL OR m.is_vegan = :isVegan) AND " +
                                        "(:isGlutenFree IS NULL OR m.is_gluten_free = :isGlutenFree) AND " +
                                        "(:isSpicy IS NULL OR m.is_spicy = :isSpicy) AND " +
                                        "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
                                        "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
                                        "(:searchTerm IS NULL OR LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR "
                                        +
                                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%'))", nativeQuery = true)
        List<MenuItemEntity> findMenuItemsWithFiltersNative(
                        @Param("vendorId") Long vendorId,
                        @Param("categoryId") Long categoryId,
                        @Param("isVegetarian") Boolean isVegetarian,
                        @Param("isVegan") Boolean isVegan,
                        @Param("isGlutenFree") Boolean isGlutenFree,
                        @Param("isSpicy") Boolean isSpicy,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("searchTerm") String searchTerm,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_available = 1 AND " +
                        "(:categoryId IS NULL OR m.category_id = :categoryId) AND " +
                        "(:isVegetarian IS NULL OR m.is_vegetarian = :isVegetarian) AND " +
                        "(:isVegan IS NULL OR m.is_vegan = :isVegan) AND " +
                        "(:isGlutenFree IS NULL OR m.is_gluten_free = :isGlutenFree) AND " +
                        "(:isSpicy IS NULL OR m.is_spicy = :isSpicy) AND " +
                        "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
                        "(:searchTerm IS NULL OR LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%'))", nativeQuery = true)
        long countMenuItemsWithFiltersNative(
                        @Param("vendorId") Long vendorId,
                        @Param("categoryId") Long categoryId,
                        @Param("isVegetarian") Boolean isVegetarian,
                        @Param("isVegan") Boolean isVegan,
                        @Param("isGlutenFree") Boolean isGlutenFree,
                        @Param("isSpicy") Boolean isSpicy,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("searchTerm") String searchTerm);

        // Search functionality using native SQL for Oracle compatibility
        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND " +
                        "(LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.ingredients) LIKE LOWER('%' || :searchTerm || '%')) " +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND "
                                        +
                                        "(LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR " +
                                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%') OR " +
                                        "LOWER(m.ingredients) LIKE LOWER('%' || :searchTerm || '%'))", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndSearchTermNative(@Param("vendorId") Long vendorId,
                        @Param("searchTerm") String searchTerm,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND " +
                        "(LOWER(m.name) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.description) LIKE LOWER('%' || :searchTerm || '%') OR " +
                        "LOWER(m.ingredients) LIKE LOWER('%' || :searchTerm || '%'))", nativeQuery = true)
        long countByVendorIdAndSearchTermNative(@Param("vendorId") Long vendorId,
                        @Param("searchTerm") String searchTerm);

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

        // Pagination support for dietary preferences using native SQL for Oracle
        // compatibility
        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegetarian = :isVegetarian "
                        +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegetarian = :isVegetarian", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndIsVegetarianNative(@Param("vendorId") Long vendorId,
                        @Param("isVegetarian") Boolean isVegetarian,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegetarian = :isVegetarian", nativeQuery = true)
        long countByVendorIdAndIsVegetarianNative(@Param("vendorId") Long vendorId,
                        @Param("isVegetarian") Boolean isVegetarian);

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegan = :isVegan " +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegan = :isVegan", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndIsVeganNative(@Param("vendorId") Long vendorId,
                        @Param("isVegan") Boolean isVegan,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_vegan = :isVegan", nativeQuery = true)
        long countByVendorIdAndIsVeganNative(@Param("vendorId") Long vendorId, @Param("isVegan") Boolean isVegan);

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_gluten_free = :isGlutenFree "
                        +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_gluten_free = :isGlutenFree", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndIsGlutenFreeNative(@Param("vendorId") Long vendorId,
                        @Param("isGlutenFree") Boolean isGlutenFree,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_gluten_free = :isGlutenFree", nativeQuery = true)
        long countByVendorIdAndIsGlutenFreeNative(@Param("vendorId") Long vendorId,
                        @Param("isGlutenFree") Boolean isGlutenFree);

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_spicy = :isSpicy " +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_spicy = :isSpicy", nativeQuery = true)
        List<MenuItemEntity> findByVendorIdAndIsSpicyNative(@Param("vendorId") Long vendorId,
                        @Param("isSpicy") Boolean isSpicy,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_spicy = :isSpicy", nativeQuery = true)
        long countByVendorIdAndIsSpicyNative(@Param("vendorId") Long vendorId, @Param("isSpicy") Boolean isSpicy);

        // Featured items with pagination using native SQL for Oracle compatibility
        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId " +
                        "ORDER BY m.created_at DESC" +
                        ") a WHERE ROWNUM <= 5" +
                        ") WHERE rnum > 0", nativeQuery = true)
        List<MenuItemEntity> findTop5ByVendorIdOrderByCreatedAtDesc(@Param("vendorId") Long vendorId);

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_featured = :isFeatured " +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= 5" +
                        ") WHERE rnum > 0", nativeQuery = true)
        List<MenuItemEntity> findTop5ByVendorIdAndIsFeaturedOrderByDisplayOrderAsc(@Param("vendorId") Long vendorId, @Param("isFeatured") Boolean isFeatured);

        @Query(value = "SELECT * FROM (" +
                        "SELECT a.*, ROWNUM rnum FROM (" +
                        "SELECT m.* FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_featured = :isFeatured "
                        +
                        "ORDER BY m.display_order ASC" +
                        ") a WHERE ROWNUM <= :maxRow" +
                        ") WHERE rnum > :minRow", countQuery = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_featured = :isFeatured", nativeQuery = true)
        List<MenuItemEntity> findTopByVendorIdAndIsFeaturedOrderByDisplayOrderAscNative(
                        @Param("vendorId") Long vendorId,
                        @Param("isFeatured") Boolean isFeatured,
                        @Param("minRow") int minRow,
                        @Param("maxRow") int maxRow);

        @Query(value = "SELECT COUNT(*) FROM ck_menu_items m WHERE m.vendor_id = :vendorId AND m.is_featured = :isFeatured", nativeQuery = true)
        long countByVendorIdAndIsFeaturedNative(@Param("vendorId") Long vendorId,
                        @Param("isFeatured") Boolean isFeatured);
}