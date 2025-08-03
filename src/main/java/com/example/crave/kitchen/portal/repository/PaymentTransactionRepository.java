package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import com.example.crave.kitchen.portal.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {

    // Basic CRUD operations
    Optional<PaymentTransactionEntity> findByTransactionId(String transactionId);

    List<PaymentTransactionEntity> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<PaymentTransactionEntity> findByPaymentMethodOrderByCreatedAtDesc(OrderEntity.PaymentMethod paymentMethod);

    List<PaymentTransactionEntity> findByStatusOrderByCreatedAtDesc(OrderEntity.PaymentStatus status);

    List<PaymentTransactionEntity> findByCurrencyOrderByCreatedAtDesc(String currency);

    // Amount queries
    List<PaymentTransactionEntity> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<PaymentTransactionEntity> findByAmountGreaterThan(BigDecimal amount);

    List<PaymentTransactionEntity> findByAmountLessThan(BigDecimal amount);

    // Date range queries
    List<PaymentTransactionEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate,
            LocalDateTime endDate);

    List<PaymentTransactionEntity> findByOrderIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long orderId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentTransactionEntity> findByStatusAndCreatedAtBetweenOrderByCreatedAtDesc(OrderEntity.PaymentStatus status,
            LocalDateTime startDate, LocalDateTime endDate);

    // Search functionality
    @Query("SELECT pt FROM PaymentTransactionEntity pt WHERE pt.transactionId LIKE %:searchTerm% OR " +
            "pt.order.orderNumber LIKE %:searchTerm% " +
            "ORDER BY pt.createdAt DESC")
    List<PaymentTransactionEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Validation methods
    boolean existsByTransactionId(String transactionId);

    boolean existsByOrderIdAndStatus(Long orderId, OrderEntity.PaymentStatus status);

    // Count methods
    long countByOrderId(Long orderId);

    long countByPaymentMethod(OrderEntity.PaymentMethod paymentMethod);

    long countByStatus(OrderEntity.PaymentStatus status);

    long countByCurrency(String currency);

    long countByOrderIdAndStatus(Long orderId, OrderEntity.PaymentStatus status);

    // Financial calculations
    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedPayments();

    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.status = 'COMPLETED' AND pt.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateCompletedPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.orderId = :orderId AND pt.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedPaymentsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.paymentMethod = :paymentMethod AND pt.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedPaymentsByMethod(@Param("paymentMethod") OrderEntity.PaymentMethod paymentMethod);

    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.status = 'FAILED'")
    BigDecimal calculateTotalFailedPayments();

    @Query("SELECT SUM(pt.amount) FROM PaymentTransactionEntity pt WHERE pt.status = 'REFUNDED'")
    BigDecimal calculateTotalRefundedPayments();

    // Payment method analytics
    @Query("SELECT pt.paymentMethod, COUNT(pt), SUM(pt.amount) FROM PaymentTransactionEntity pt " +
            "WHERE pt.status = 'COMPLETED' GROUP BY pt.paymentMethod ORDER BY SUM(pt.amount) DESC")
    List<Object[]> findPaymentMethodAnalytics();

    // Success rate calculations
    @Query("SELECT COUNT(pt) FROM PaymentTransactionEntity pt WHERE pt.status = 'COMPLETED'")
    long countCompletedTransactions();

    @Query("SELECT COUNT(pt) FROM PaymentTransactionEntity pt WHERE pt.status = 'FAILED'")
    long countFailedTransactions();

    @Query("SELECT COUNT(pt) FROM PaymentTransactionEntity pt WHERE pt.status = 'PENDING'")
    long countPendingTransactions();

    @Query("SELECT COUNT(pt) FROM PaymentTransactionEntity pt WHERE pt.status = 'REFUNDED'")
    long countRefundedTransactions();

    // Recent transactions
    List<PaymentTransactionEntity> findTop10ByOrderByCreatedAtDesc();

    List<PaymentTransactionEntity> findTop10ByStatusOrderByCreatedAtDesc(OrderEntity.PaymentStatus status);

    List<PaymentTransactionEntity> findTop10ByPaymentMethodOrderByCreatedAtDesc(
            OrderEntity.PaymentMethod paymentMethod);

    // Failed transactions for retry
    List<PaymentTransactionEntity> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(OrderEntity.PaymentStatus status,
            LocalDateTime beforeDate);

    // Transaction by amount range and status
    List<PaymentTransactionEntity> findByAmountBetweenAndStatusOrderByCreatedAtDesc(BigDecimal minAmount,
            BigDecimal maxAmount, OrderEntity.PaymentStatus status);
}