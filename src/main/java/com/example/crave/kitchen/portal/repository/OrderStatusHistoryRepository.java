package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {

    // Basic CRUD operations
    List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtAsc(Long orderId);

    List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtDesc(Long orderId);

    List<OrderStatusHistoryEntity> findByStatusOrderByChangedAtDesc(
            OrderStatusHistoryEntity.OrderEntity.OrderStatus status);

    List<OrderStatusHistoryEntity> findByChangedByOrderByChangedAtDesc(String changedBy);

    // Date range queries
    List<OrderStatusHistoryEntity> findByChangedAtBetweenOrderByChangedAtDesc(LocalDateTime startDate,
            LocalDateTime endDate);

    List<OrderStatusHistoryEntity> findByOrderIdAndChangedAtBetweenOrderByChangedAtDesc(Long orderId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<OrderStatusHistoryEntity> findByStatusAndChangedAtBetweenOrderByChangedAtDesc(
            OrderStatusHistoryEntity.OrderEntity.OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Latest status for each order
    @Query("SELECT osh FROM OrderStatusHistoryEntity osh WHERE osh.orderId = :orderId AND osh.changedAt = " +
            "(SELECT MAX(osh2.changedAt) FROM OrderStatusHistoryEntity osh2 WHERE osh2.orderId = :orderId)")
    OrderStatusHistoryEntity findLatestStatusByOrderId(@Param("orderId") Long orderId);

    // Status transitions
    @Query("SELECT osh FROM OrderStatusHistoryEntity osh WHERE osh.orderId = :orderId AND osh.status = :status " +
            "ORDER BY osh.changedAt DESC")
    List<OrderStatusHistoryEntity> findByOrderIdAndStatusOrderByChangedAtDesc(@Param("orderId") Long orderId,
            @Param("status") OrderStatusHistoryEntity.OrderEntity.OrderStatus status);

    // Count methods
    long countByOrderId(Long orderId);

    long countByStatus(OrderStatusHistoryEntity.OrderEntity.OrderStatus status);

    long countByChangedBy(String changedBy);

    long countByOrderIdAndStatus(Long orderId, OrderStatusHistoryEntity.OrderEntity.OrderStatus status);

    // Time-based analytics
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (osh2.changedAt - osh1.changedAt))/3600) " +
            "FROM OrderStatusHistoryEntity osh1, OrderStatusHistoryEntity osh2 " +
            "WHERE osh1.orderId = osh2.orderId AND osh1.status = :fromStatus AND osh2.status = :toStatus " +
            "AND osh1.changedAt < osh2.changedAt")
    Double calculateAverageTransitionTime(
            @Param("fromStatus") OrderStatusHistoryEntity.OrderEntity.OrderStatus fromStatus,
            @Param("toStatus") OrderStatusHistoryEntity.OrderEntity.OrderStatus toStatus);

    // Status change frequency
    @Query("SELECT osh.status, COUNT(osh) FROM OrderStatusHistoryEntity osh " +
            "WHERE osh.changedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY osh.status ORDER BY COUNT(osh) DESC")
    List<Object[]> findStatusChangeFrequency(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Recent status changes
    List<OrderStatusHistoryEntity> findTop10ByOrderByChangedAtDesc();

    List<OrderStatusHistoryEntity> findTop10ByChangedByOrderByChangedAtDesc(String changedBy);

    // Validation methods
    boolean existsByOrderIdAndStatus(Long orderId, OrderStatusHistoryEntity.OrderEntity.OrderStatus status);
}