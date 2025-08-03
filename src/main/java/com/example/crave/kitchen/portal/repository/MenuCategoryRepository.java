package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.MenuCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategoryEntity, Long> {

    // Basic CRUD operations
    List<MenuCategoryEntity> findByVendorIdAndIsActiveOrderByDisplayOrderAsc(Long vendorId, Boolean isActive);

    List<MenuCategoryEntity> findByVendorIdOrderByDisplayOrderAsc(Long vendorId);

    Optional<MenuCategoryEntity> findByVendorIdAndNameAndIsActive(Long vendorId, String name, Boolean isActive);

    // Pagination support - using custom queries for Oracle compatibility
    @Query(value = "SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (SELECT * FROM ck_menu_categories WHERE vendor_id = :vendorId ORDER BY display_order ASC) a WHERE ROWNUM <= :maxRow) WHERE rnum > :minRow", nativeQuery = true)
    List<MenuCategoryEntity> findByVendorIdWithPagination(@Param("vendorId") Long vendorId, @Param("minRow") int minRow, @Param("maxRow") int maxRow);

    @Query(value = "SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (SELECT * FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_active = :isActive ORDER BY display_order ASC) a WHERE ROWNUM <= :maxRow) WHERE rnum > :minRow", nativeQuery = true)
    List<MenuCategoryEntity> findByVendorIdAndIsActiveWithPagination(@Param("vendorId") Long vendorId, @Param("isActive") Boolean isActive, @Param("minRow") int minRow, @Param("maxRow") int maxRow);

    @Query(value = "SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (SELECT * FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_featured = :isFeatured ORDER BY display_order ASC) a WHERE ROWNUM <= :maxRow) WHERE rnum > :minRow", nativeQuery = true)
    List<MenuCategoryEntity> findByVendorIdAndIsFeaturedWithPagination(@Param("vendorId") Long vendorId, @Param("isFeatured") Boolean isFeatured, @Param("minRow") int minRow, @Param("maxRow") int maxRow);

    @Query(value = "SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (SELECT * FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_active = :isActive AND is_featured = :isFeatured ORDER BY display_order ASC) a WHERE ROWNUM <= :maxRow) WHERE rnum > :minRow", nativeQuery = true)
    List<MenuCategoryEntity> findByVendorIdAndIsActiveAndIsFeaturedWithPagination(@Param("vendorId") Long vendorId, @Param("isActive") Boolean isActive, @Param("isFeatured") Boolean isFeatured, @Param("minRow") int minRow, @Param("maxRow") int maxRow);

    // Keep original methods for backward compatibility but they may not work with pagination
    Page<MenuCategoryEntity> findByVendorId(Long vendorId, Pageable pageable);

    Page<MenuCategoryEntity> findByVendorIdAndIsActive(Long vendorId, Boolean isActive, Pageable pageable);

    Page<MenuCategoryEntity> findByVendorIdAndIsFeatured(Long vendorId, Boolean isFeatured, Pageable pageable);

    Page<MenuCategoryEntity> findByVendorIdAndIsActiveAndIsFeatured(Long vendorId, Boolean isActive, Boolean isFeatured,
            Pageable pageable);

    // Featured categories
    @Query("SELECT c FROM MenuCategoryEntity c WHERE c.vendorId = :vendorId AND c.isActive = true AND c.isFeatured = true ORDER BY c.displayOrder ASC")
    List<MenuCategoryEntity> findFeaturedCategoriesByVendorId(@Param("vendorId") Long vendorId);

    // Validation methods
    boolean existsByVendorIdAndNameAndIdNot(Long vendorId, String name, Long id);

    boolean existsByVendorIdAndName(Long vendorId, String name);

    // Count methods
    @Query("SELECT COUNT(c) FROM MenuCategoryEntity c WHERE c.vendorId = :vendorId AND c.isActive = true")
    long countActiveCategoriesByVendorId(@Param("vendorId") Long vendorId);

    long countByVendorId(Long vendorId);

    long countByVendorIdAndIsActive(Long vendorId, Boolean isActive);

    long countByVendorIdAndIsFeatured(Long vendorId, Boolean isFeatured);

    // Count methods for pagination
    @Query(value = "SELECT COUNT(*) FROM ck_menu_categories WHERE vendor_id = :vendorId", nativeQuery = true)
    long countByVendorIdNative(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT COUNT(*) FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_active = :isActive", nativeQuery = true)
    long countByVendorIdAndIsActiveNative(@Param("vendorId") Long vendorId, @Param("isActive") Boolean isActive);

    @Query(value = "SELECT COUNT(*) FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_featured = :isFeatured", nativeQuery = true)
    long countByVendorIdAndIsFeaturedNative(@Param("vendorId") Long vendorId, @Param("isFeatured") Boolean isFeatured);

    @Query(value = "SELECT COUNT(*) FROM ck_menu_categories WHERE vendor_id = :vendorId AND is_active = :isActive AND is_featured = :isFeatured", nativeQuery = true)
    long countByVendorIdAndIsActiveAndIsFeaturedNative(@Param("vendorId") Long vendorId, @Param("isActive") Boolean isActive, @Param("isFeatured") Boolean isFeatured);
}