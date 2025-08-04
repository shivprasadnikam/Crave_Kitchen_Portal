package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.dto.OrderDto;
import com.example.crave.kitchen.portal.dto.OrderFilterRequestDto;
import com.example.crave.kitchen.portal.dto.UpdateOrderStatusRequestDto;
import com.example.crave.kitchen.portal.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    /**
     * Get all orders with filters and pagination
     */
    Page<OrderDto> getAllOrders(OrderFilterRequestDto filterRequest, Long vendorId);

    /**
     * Get order by ID
     */
    OrderDto getOrderById(Long orderId, Long vendorId);

    /**
     * Update order status
     */
    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto request, Long vendorId);

    /**
     * Get order history for a vendor
     */
    List<OrderDto> getOrderHistory(Long vendorId, OrderFilterRequestDto filterRequest);

    /**
     * Get order by order number
     */
    OrderDto getOrderByOrderNumber(String orderNumber, Long vendorId);

    // Enhanced endpoints for specific order statuses
    /**
     * Get pending orders only
     */
    Page<OrderDto> getPendingOrders(Long vendorId, Pageable pageable);

    /**
     * Get orders being prepared
     */
    Page<OrderDto> getPreparingOrders(Long vendorId, Pageable pageable);

    /**
     * Get ready orders
     */
    Page<OrderDto> getReadyOrders(Long vendorId, Pageable pageable);

    /**
     * Get completed orders
     */
    Page<OrderDto> getCompletedOrders(Long vendorId, Pageable pageable);

    /**
     * Get cancelled orders
     */
    Page<OrderDto> getCancelledOrders(Long vendorId, Pageable pageable);

    // Order action methods
    /**
     * Accept order
     */
    OrderDto acceptOrder(Long orderId, String notes, Long vendorId);

    /**
     * Reject order
     */
    OrderDto rejectOrder(Long orderId, String reason, Long vendorId);

    /**
     * Start preparing order
     */
    OrderDto startPreparingOrder(Long orderId, Integer estimatedTime, String notes, Long vendorId);

    /**
     * Mark order as ready
     */
    OrderDto markOrderReady(Long orderId, Integer actualTime, String notes, Long vendorId);

    /**
     * Complete order
     */
    OrderDto completeOrder(Long orderId, String notes, Long vendorId);

    // Enhanced search and filter methods
    /**
     * Search orders by term
     */
    Page<OrderDto> searchOrders(String searchTerm, Long vendorId, Pageable pageable);

    /**
     * Filter orders by multiple criteria
     */
    Page<OrderDto> filterOrders(OrderFilterRequestDto filterRequest, Long vendorId, Pageable pageable);
}