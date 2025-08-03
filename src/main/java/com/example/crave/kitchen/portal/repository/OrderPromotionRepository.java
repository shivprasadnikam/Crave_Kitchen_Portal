package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderPromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotionEntity, Long> {

    // Basic CRUD operations
    List<OrderPromotionEntity> findByOrderIdOrderByAppliedAtDesc(Long orderId);

    List<OrderPromotionEntity> findByPromotionIdOrderByAppliedAtDesc(Long promotionId);

    List<OrderPromotionEntity> findByCreatedByOrderByAppliedAtDesc(String createdBy);

    // Amount queries
    List<OrderPromotionEntity> findByDiscountAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<OrderPromotionEntity> findByDiscountAmountGreaterThan(BigDecimal amount);

    List<OrderPromotionEntity> findByDiscountAmountLessThan(BigDecimal amount);

    // Date range queries
    List<OrderPromotionEntity> findByAppliedAtBetweenOrderByAppliedAtDesc(LocalDateTime startDate,
            LocalDateTime endDate);

    List<OrderPromotionEntity> findByOrderIdAndAppliedAtBetweenOrderByAppliedAtDesc(Long orderId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<OrderPromotionEntity> findByPromotionIdAndAppliedAtBetweenOrderByAppliedAtDesc(Long promotionId,
            LocalDateTime startDate, LocalDateTime endDate);

    // Search functionality
    @Query("SELECT op FROM OrderPromotionEntity op WHERE op.order.orderNumber LIKE %:searchTerm% OR " +
            "op.promotion.promotionCode LIKE %:searchTerm% OR " +
            "op.promotion.promotionName LIKE %:searchTerm% " +
            "ORDER BY op.appliedAt DESC")
    List<OrderPromotionEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Validation methods
    boolean existsByOrderIdAndPromotionId(Long orderId, Long promotionId);

    // Count methods
    long countByOrderId(Long orderId);

    long countByPromotionId(Long promotionId);

    long countByCreatedBy(String createdBy);

    long countByDiscountAmountGreaterThan(BigDecimal amount);

    // Financial calculations
    @Query("SELECT SUM(op.discountAmount) FROM OrderPromotionEntity op")
    BigDecimal calculateTotalDiscountAmount();

    @Query("SELECT SUM(op.discountAmount) FROM OrderPromotionEntity op WHERE op.orderId = :orderId")
    BigDecimal calculateTotalDiscountAmountByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(op.discountAmount) FROM OrderPromotionEntity op WHERE op.promotionId = :promotionId")
    BigDecimal calculateTotalDiscountAmountByPromotionId(@Param("promotionId") Long promotionId);

    @Query("SELECT SUM(op.discountAmount) FROM OrderPromotionEntity op WHERE op.appliedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalDiscountAmountByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Average discount calculations
    @Query("SELECT AVG(op.discountAmount) FROM OrderPromotionEntity op")
    BigDecimal calculateAverageDiscountAmount();

    @Query("SELECT AVG(op.discountAmount) FROM OrderPromotionEntity op WHERE op.promotionId = :promotionId")
    BigDecimal calculateAverageDiscountAmountByPromotionId(@Param("promotionId") Long promotionId);

    // Promotion usage analytics
    @Query("SELECT op.promotionId, COUNT(op), SUM(op.discountAmount) FROM OrderPromotionEntity op " +
            "GROUP BY op.promotionId ORDER BY COUNT(op) DESC")
    List<Object[]> findPromotionUsageAnalytics();

    @Query("SELECT op.promotionId, COUNT(op), SUM(op.discountAmount) FROM OrderPromotionEntity op " +
            "WHERE op.appliedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY op.promotionId ORDER BY COUNT(op) DESC")
    List<Object[]> findPromotionUsageAnalyticsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Recent applications
    List<OrderPromotionEntity> findTop10ByOrderByAppliedAtDesc();

    List<OrderPromotionEntity> findTop10ByOrderIdOrderByAppliedAtDesc(Long orderId);

    List<OrderPromotionEntity> findTop10ByPromotionIdOrderByAppliedAtDesc(Long promotionId);

    // Highest discount applications
    List<OrderPromotionEntity> findTop10ByOrderByDiscountAmountDesc();

    List<OrderPromotionEntity> findTop10ByPromotionIdOrderByDiscountAmountDesc(Long promotionId);

    // Applications by user
    List<OrderPromotionEntity> findTop10ByCreatedByOrderByAppliedAtDesc(String createdBy);

    // Applications by amount range
    List<OrderPromotionEntity> findByDiscountAmountBetweenOrderByAppliedAtDesc(BigDecimal minAmount,
            BigDecimal maxAmount);

    List<OrderPromotionEntity> findByDiscountAmountBetweenAndPromotionIdOrderByAppliedAtDesc(BigDecimal minAmount,
            BigDecimal maxAmount, Long promotionId);
}