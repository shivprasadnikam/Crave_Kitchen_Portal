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

    // Pagination support
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
}