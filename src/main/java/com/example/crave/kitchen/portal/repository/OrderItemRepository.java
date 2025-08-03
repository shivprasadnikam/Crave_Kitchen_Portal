package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    // Basic CRUD operations
    List<OrderItemEntity> findByOrderIdOrderByCreatedAtAsc(Long orderId);

    List<OrderItemEntity> findByMenuItemIdOrderByCreatedAtDesc(Long menuItemId);

    List<OrderItemEntity> findByOrderIdAndMenuItemId(Long orderId, Long menuItemId);

    // Quantity queries
    List<OrderItemEntity> findByQuantityGreaterThan(Integer quantity);

    List<OrderItemEntity> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);

    // Price queries
    List<OrderItemEntity> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<OrderItemEntity> findByTotalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<OrderItemEntity> findByUnitPriceGreaterThan(BigDecimal price);

    List<OrderItemEntity> findByTotalPriceGreaterThan(BigDecimal price);

    // Search functionality
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.specialInstructions LIKE %:searchTerm% OR " +
            "oi.menuItem.name LIKE %:searchTerm% OR " +
            "oi.menuItem.description LIKE %:searchTerm% " +
            "ORDER BY oi.createdAt DESC")
    List<OrderItemEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Count methods
    long countByOrderId(Long orderId);

    long countByMenuItemId(Long menuItemId);

    long countByQuantityGreaterThan(Integer quantity);

    // Sum calculations
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItemEntity oi WHERE oi.orderId = :orderId")
    BigDecimal calculateTotalAmountByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItemEntity oi WHERE oi.orderId = :orderId")
    Integer calculateTotalQuantityByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItemEntity oi WHERE oi.menuItemId = :menuItemId")
    BigDecimal calculateTotalRevenueByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItemEntity oi WHERE oi.menuItemId = :menuItemId")
    Integer calculateTotalQuantitySoldByMenuItemId(@Param("menuItemId") Long menuItemId);

    // Popular items
    @Query("SELECT oi.menuItemId, SUM(oi.quantity) as totalQuantity FROM OrderItemEntity oi " +
            "GROUP BY oi.menuItemId ORDER BY totalQuantity DESC")
    List<Object[]> findMostPopularMenuItems();

    @Query("SELECT oi.menuItemId, SUM(oi.totalPrice) as totalRevenue FROM OrderItemEntity oi " +
            "GROUP BY oi.menuItemId ORDER BY totalRevenue DESC")
    List<Object[]> findHighestRevenueMenuItems();

    // Items with special instructions
    List<OrderItemEntity> findBySpecialInstructionsIsNotNullOrderByCreatedAtDesc();

    List<OrderItemEntity> findBySpecialInstructionsContainingIgnoreCase(String instruction);

    // Items with customizations
    List<OrderItemEntity> findByCustomizationOptionsIsNotNullOrderByCreatedAtDesc();

    List<OrderItemEntity> findByCustomizationOptionsContainingIgnoreCase(String customization);

    // Validation methods
    boolean existsByOrderIdAndMenuItemId(Long orderId, Long menuItemId);
}