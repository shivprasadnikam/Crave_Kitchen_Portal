package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.PromotionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    // Basic CRUD operations
    Optional<PromotionEntity> findByPromotionCode(String promotionCode);

    List<PromotionEntity> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<PromotionEntity> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);

    List<PromotionEntity> findByDiscountTypeOrderByCreatedAtDesc(PromotionEntity.DiscountType discountType);

    List<PromotionEntity> findByVendorIdAndIsActiveOrderByCreatedAtDesc(Long vendorId, Boolean isActive);

    // Date range queries
    List<PromotionEntity> findByValidFromBetweenOrderByValidFromAsc(LocalDateTime startDate, LocalDateTime endDate);

    List<PromotionEntity> findByValidUntilBetweenOrderByValidUntilAsc(LocalDateTime startDate, LocalDateTime endDate);

    List<PromotionEntity> findByValidFromBeforeAndValidUntilAfterOrderByValidFromAsc(LocalDateTime beforeDate,
            LocalDateTime afterDate);

    List<PromotionEntity> findByVendorIdAndValidFromBeforeAndValidUntilAfterOrderByValidFromAsc(Long vendorId,
            LocalDateTime beforeDate, LocalDateTime afterDate);

    // Amount queries
    List<PromotionEntity> findByDiscountValueBetween(BigDecimal minValue, BigDecimal maxValue);

    List<PromotionEntity> findByMinimumOrderAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<PromotionEntity> findByMaximumDiscountAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<PromotionEntity> findByMinimumOrderAmountLessThanEqual(BigDecimal amount);

    List<PromotionEntity> findByMaximumDiscountAmountGreaterThanEqual(BigDecimal amount);

    // Usage queries
    List<PromotionEntity> findByUsageLimitIsNotNullOrderByCreatedAtDesc();

    List<PromotionEntity> findByUsageCountGreaterThan(Integer count);

    List<PromotionEntity> findByUsageLimitGreaterThanUsageCountOrderByCreatedAtDesc();

    // Advanced queries with pagination
    Page<PromotionEntity> findByVendorId(Long vendorId, Pageable pageable);

    Page<PromotionEntity> findByIsActive(Boolean isActive, Pageable pageable);

    Page<PromotionEntity> findByVendorIdAndIsActive(Long vendorId, Boolean isActive, Pageable pageable);

    // Search functionality
    @Query("SELECT p FROM PromotionEntity p WHERE p.promotionName LIKE %:searchTerm% OR " +
            "p.description LIKE %:searchTerm% OR " +
            "p.promotionCode LIKE %:searchTerm% " +
            "ORDER BY p.createdAt DESC")
    List<PromotionEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Validation methods
    boolean existsByPromotionCode(String promotionCode);

    boolean existsByVendorIdAndPromotionCode(Long vendorId, String promotionCode);

    boolean existsByPromotionCodeAndIdNot(String promotionCode, Long id);

    // Count methods
    long countByVendorId(Long vendorId);

    long countByIsActive(Boolean isActive);

    long countByDiscountType(PromotionEntity.DiscountType discountType);

    long countByVendorIdAndIsActive(Long vendorId, Boolean isActive);

    long countByUsageLimitGreaterThanUsageCount();

    // Financial calculations
    @Query("SELECT SUM(p.discountValue) FROM PromotionEntity p WHERE p.isActive = true")
    BigDecimal calculateTotalActiveDiscountValue();

    @Query("SELECT SUM(p.discountValue) FROM PromotionEntity p WHERE p.vendorId = :vendorId AND p.isActive = true")
    BigDecimal calculateTotalActiveDiscountValueByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT AVG(p.discountValue) FROM PromotionEntity p WHERE p.isActive = true")
    BigDecimal calculateAverageDiscountValue();

    @Query("SELECT AVG(p.discountValue) FROM PromotionEntity p WHERE p.vendorId = :vendorId AND p.isActive = true")
    BigDecimal calculateAverageDiscountValueByVendorId(@Param("vendorId") Long vendorId);

    // Usage analytics
    @Query("SELECT AVG(p.usageCount) FROM PromotionEntity p WHERE p.usageLimit IS NOT NULL")
    Double calculateAverageUsageCount();

    @Query("SELECT AVG(p.usageCount) FROM PromotionEntity p WHERE p.vendorId = :vendorId AND p.usageLimit IS NOT NULL")
    Double calculateAverageUsageCountByVendorId(@Param("vendorId") Long vendorId);

    // Expiring promotions
    List<PromotionEntity> findByValidUntilBeforeAndIsActiveOrderByValidUntilAsc(LocalDateTime date, Boolean isActive);

    List<PromotionEntity> findByVendorIdAndValidUntilBeforeAndIsActiveOrderByValidUntilAsc(Long vendorId,
            LocalDateTime date, Boolean isActive);

    // Recent promotions
    List<PromotionEntity> findTop10ByOrderByCreatedAtDesc();

    List<PromotionEntity> findTop10ByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<PromotionEntity> findTop10ByIsActiveOrderByCreatedAtDesc(Boolean isActive);

    // Most used promotions
    @Query("SELECT p FROM PromotionEntity p WHERE p.usageLimit IS NOT NULL ORDER BY p.usageCount DESC")
    List<PromotionEntity> findMostUsedPromotions();

    @Query("SELECT p FROM PromotionEntity p WHERE p.vendorId = :vendorId AND p.usageLimit IS NOT NULL ORDER BY p.usageCount DESC")
    List<PromotionEntity> findMostUsedPromotionsByVendorId(@Param("vendorId") Long vendorId);

    // Available promotions (not expired, active, and within usage limit)
    @Query("SELECT p FROM PromotionEntity p WHERE p.isActive = true AND p.validUntil > :currentDate AND " +
            "(p.usageLimit IS NULL OR p.usageCount < p.usageLimit) ORDER BY p.createdAt DESC")
    List<PromotionEntity> findAvailablePromotions(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM PromotionEntity p WHERE p.vendorId = :vendorId AND p.isActive = true AND p.validUntil > :currentDate AND "
            +
            "(p.usageLimit IS NULL OR p.usageCount < p.usageLimit) ORDER BY p.createdAt DESC")
    List<PromotionEntity> findAvailablePromotionsByVendorId(@Param("vendorId") Long vendorId,
            @Param("currentDate") LocalDateTime currentDate);
}