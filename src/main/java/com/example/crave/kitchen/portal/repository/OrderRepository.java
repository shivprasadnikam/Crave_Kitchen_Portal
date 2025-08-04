package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderEntity;
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
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, OrderRepositoryCustom {

        // Basic CRUD operations
        Optional<OrderEntity> findByOrderNumber(String orderNumber);

        List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

        List<OrderEntity> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

        List<OrderEntity> findByCustomerIdAndOrderStatusOrderByCreatedAtDesc(Long customerId,
                        OrderEntity.OrderStatus orderStatus);

        List<OrderEntity> findByVendorIdAndOrderStatusOrderByCreatedAtDesc(Long vendorId,
                        OrderEntity.OrderStatus orderStatus);

        List<OrderEntity> findByOrderStatusOrderByCreatedAtDesc(OrderEntity.OrderStatus orderStatus);

        List<OrderEntity> findByPaymentStatusOrderByCreatedAtDesc(OrderEntity.PaymentStatus paymentStatus);

        List<OrderEntity> findByOrderTypeOrderByCreatedAtDesc(OrderEntity.OrderType orderType);

        // Advanced queries with pagination
        Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);

        Page<OrderEntity> findByVendorId(Long vendorId, Pageable pageable);

        Page<OrderEntity> findByCustomerIdAndOrderStatus(Long customerId, OrderEntity.OrderStatus orderStatus,
                        Pageable pageable);

        Page<OrderEntity> findByVendorIdAndOrderStatus(Long vendorId, OrderEntity.OrderStatus orderStatus,
                        Pageable pageable);

        // Date range queries
        List<OrderEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

        List<OrderEntity> findByCustomerIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long customerId,
                        LocalDateTime startDate,
                        LocalDateTime endDate);

        List<OrderEntity> findByVendorIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long vendorId, LocalDateTime startDate,
                        LocalDateTime endDate);

        // Amount range queries
        List<OrderEntity> findByTotalAmountBetweenOrderByCreatedAtDesc(BigDecimal minAmount, BigDecimal maxAmount);

        List<OrderEntity> findByCustomerIdAndTotalAmountBetweenOrderByCreatedAtDesc(Long customerId,
                        BigDecimal minAmount,
                        BigDecimal maxAmount);

        List<OrderEntity> findByVendorIdAndTotalAmountBetweenOrderByCreatedAtDesc(Long vendorId, BigDecimal minAmount,
                        BigDecimal maxAmount);

        // Pickup time queries
        List<OrderEntity> findByPickupTimeBetweenOrderByPickupTimeAsc(LocalDateTime startTime, LocalDateTime endTime);

        List<OrderEntity> findByVendorIdAndPickupTimeBetweenOrderByPickupTimeAsc(Long vendorId, LocalDateTime startTime,
                        LocalDateTime endTime);

        // Search functionality
        @Query("SELECT o FROM OrderEntity o WHERE o.orderNumber LIKE %:searchTerm% OR " +
                        "o.customer.firstName LIKE %:searchTerm% OR " +
                        "o.customer.lastName LIKE %:searchTerm% OR " +
                        "o.customer.email LIKE %:searchTerm% " +
                        "ORDER BY o.createdAt DESC")
        List<OrderEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

        // Validation methods
        boolean existsByOrderNumber(String orderNumber);

        // Count methods
        long countByCustomerId(Long customerId);

        long countByVendorId(Long vendorId);

        long countByCustomerIdAndOrderStatus(Long customerId, OrderEntity.OrderStatus orderStatus);

        long countByVendorIdAndOrderStatus(Long vendorId, OrderEntity.OrderStatus orderStatus);

        long countByOrderStatus(OrderEntity.OrderStatus orderStatus);

        long countByPaymentStatus(OrderEntity.PaymentStatus paymentStatus);

        long countByOrderType(OrderEntity.OrderType orderType);

        // Revenue calculations
        @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.paymentStatus = 'PAID'")
        BigDecimal calculateTotalRevenueByVendorId(@Param("vendorId") Long vendorId);

        @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.paymentStatus = 'PAID' AND o.createdAt BETWEEN :startDate AND :endDate")
        BigDecimal calculateRevenueByVendorIdAndDateRange(@Param("vendorId") Long vendorId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Average order value
        @Query("SELECT AVG(o.totalAmount) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.paymentStatus = 'PAID'")
        BigDecimal calculateAverageOrderValueByVendorId(@Param("vendorId") Long vendorId);

        // Recent orders
        List<OrderEntity> findTop10ByVendorIdOrderByCreatedAtDesc(Long vendorId);

        List<OrderEntity> findTop10ByCustomerIdOrderByCreatedAtDesc(Long customerId);

        // Orders by preparation time
        List<OrderEntity> findByVendorIdAndEstimatedPreparationTimeGreaterThanOrderByCreatedAtDesc(Long vendorId,
                        Integer minutes);

        List<OrderEntity> findByVendorIdAndActualPreparationTimeGreaterThanOrderByCreatedAtDesc(Long vendorId,
                        Integer minutes);
}