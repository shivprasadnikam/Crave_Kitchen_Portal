package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.dto.OrderDto;
import com.example.crave.kitchen.portal.dto.OrderFilterRequestDto;
import com.example.crave.kitchen.portal.dto.UpdateOrderStatusRequestDto;
import com.example.crave.kitchen.portal.entity.OrderEntity;
import com.example.crave.kitchen.portal.entity.OrderStatusHistoryEntity;
import com.example.crave.kitchen.portal.repository.OrderRepository;
import com.example.crave.kitchen.portal.repository.OrderStatusHistoryRepository;
import com.example.crave.kitchen.portal.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    public Page<OrderDto> getAllOrders(OrderFilterRequestDto filterRequest, Long vendorId) {
        log.info("Getting all orders for vendor: {} with filters: {}", vendorId, filterRequest);

        // Create pageable with sorting
        Sort sort = Sort.by(
                filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        // Build query based on filters
        Page<OrderEntity> ordersPage;

        if (filterRequest.getOrderStatus() != null) {
            ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                    vendorId, filterRequest.getOrderStatus(), pageable);
        } else {
            ordersPage = orderRepository.findByVendorIdWithPagination(vendorId, pageable);
        }

        return ordersPage.map(this::convertToDto);
    }

    @Override
    public OrderDto getOrderById(Long orderId, Long vendorId) {
        log.info("Getting order by ID: {} for vendor: {}", orderId, vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        return convertToDto(order);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto request, Long vendorId) {
        log.info("Updating order status for order: {} to: {} by vendor: {}",
                orderId, request.getOrderStatus(), vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status
        order.setOrderStatus(request.getOrderStatus());

        // Update preparation times if provided
        if (request.getEstimatedPreparationTime() != null) {
            order.setEstimatedPreparationTime(request.getEstimatedPreparationTime());
        }
        if (request.getActualPreparationTime() != null) {
            order.setActualPreparationTime(request.getActualPreparationTime());
        }

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(request.getOrderStatus())
                .statusDescription(request.getNotes())
                .changedBy("vendor_" + vendorId) // In a real app, get from security context
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order status updated successfully for order: {}", orderId);
        return convertToDto(savedOrder);
    }

    @Override
    public List<OrderDto> getOrderHistory(Long vendorId, OrderFilterRequestDto filterRequest) {
        log.info("Getting order history for vendor: {} with filters: {}", vendorId, filterRequest);

        List<OrderEntity> orders;

        if (filterRequest.getOrderStatus() != null) {
            orders = orderRepository.findByVendorIdAndOrderStatusOrderByCreatedAtDesc(
                    vendorId, filterRequest.getOrderStatus());
        } else {
            orders = orderRepository.findByVendorIdOrderByCreatedAtDesc(vendorId);
        }

        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderByOrderNumber(String orderNumber, Long vendorId) {
        log.info("Getting order by order number: {} for vendor: {}", orderNumber, vendorId);

        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        return convertToDto(order);
    }

    /**
     * Convert OrderEntity to OrderDto
     */
    private OrderDto convertToDto(OrderEntity order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .customerEmail(order.getCustomer().getEmail())
                .vendorId(order.getVendor().getId())
                .vendorName(order.getVendor().getRestaurantName())
                .orderStatus(order.getOrderStatus())
                .orderType(order.getOrderType())
                .totalAmount(order.getTotalAmount())
                .subtotalAmount(order.getSubtotalAmount())
                .taxAmount(order.getTaxAmount())
                .tipAmount(order.getTipAmount())
                .discountAmount(order.getDiscountAmount())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .estimatedPreparationTime(order.getEstimatedPreparationTime())
                .actualPreparationTime(order.getActualPreparationTime())
                .pickupTime(order.getPickupTime())
                .tableNumber(order.getTableNumber())
                .orderNotes(order.getOrderNotes())
                .customerNotes(order.getCustomerNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    // Enhanced endpoints for specific order statuses
    @Override
    public Page<OrderDto> getPendingOrders(Long vendorId, Pageable pageable) {
        log.info("Getting pending orders for vendor: {}", vendorId);
        Page<OrderEntity> ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                vendorId, OrderEntity.OrderStatus.PENDING, pageable);
        return ordersPage.map(this::convertToDto);
    }

    @Override
    public Page<OrderDto> getPreparingOrders(Long vendorId, Pageable pageable) {
        log.info("Getting preparing orders for vendor: {}", vendorId);
        Page<OrderEntity> ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                vendorId, OrderEntity.OrderStatus.PREPARING, pageable);
        return ordersPage.map(this::convertToDto);
    }

    @Override
    public Page<OrderDto> getReadyOrders(Long vendorId, Pageable pageable) {
        log.info("Getting ready orders for vendor: {}", vendorId);
        Page<OrderEntity> ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                vendorId, OrderEntity.OrderStatus.READY, pageable);
        return ordersPage.map(this::convertToDto);
    }

    @Override
    public Page<OrderDto> getCompletedOrders(Long vendorId, Pageable pageable) {
        log.info("Getting completed orders for vendor: {}", vendorId);
        Page<OrderEntity> ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                vendorId, OrderEntity.OrderStatus.COMPLETED, pageable);
        return ordersPage.map(this::convertToDto);
    }

    @Override
    public Page<OrderDto> getCancelledOrders(Long vendorId, Pageable pageable) {
        log.info("Getting cancelled orders for vendor: {}", vendorId);
        Page<OrderEntity> ordersPage = orderRepository.findByVendorIdAndOrderStatusWithPagination(
                vendorId, OrderEntity.OrderStatus.CANCELLED, pageable);
        return ordersPage.map(this::convertToDto);
    }

    // Order action methods
    @Override
    public OrderDto acceptOrder(Long orderId, String notes, Long vendorId) {
        log.info("Accepting order: {} by vendor: {}", orderId, vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Verify order is in PENDING status
        if (order.getOrderStatus() != OrderEntity.OrderStatus.PENDING) {
            throw new RuntimeException("Order must be in PENDING status to be accepted");
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status to CONFIRMED
        order.setOrderStatus(OrderEntity.OrderStatus.CONFIRMED);

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(OrderEntity.OrderStatus.CONFIRMED)
                .statusDescription(notes != null ? notes : "Order accepted")
                .changedBy("vendor_" + vendorId)
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order accepted successfully: {}", orderId);
        return convertToDto(savedOrder);
    }

    @Override
    public OrderDto rejectOrder(Long orderId, String reason, Long vendorId) {
        log.info("Rejecting order: {} by vendor: {} with reason: {}", orderId, vendorId, reason);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Verify order is in PENDING status
        if (order.getOrderStatus() != OrderEntity.OrderStatus.PENDING) {
            throw new RuntimeException("Order must be in PENDING status to be rejected");
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status to CANCELLED
        order.setOrderStatus(OrderEntity.OrderStatus.CANCELLED);

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(OrderEntity.OrderStatus.CANCELLED)
                .statusDescription("Order rejected: " + reason)
                .changedBy("vendor_" + vendorId)
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order rejected successfully: {}", orderId);
        return convertToDto(savedOrder);
    }

    @Override
    public OrderDto startPreparingOrder(Long orderId, Integer estimatedTime, String notes, Long vendorId) {
        log.info("Starting preparation for order: {} by vendor: {}", orderId, vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Verify order is in CONFIRMED status
        if (order.getOrderStatus() != OrderEntity.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order must be in CONFIRMED status to start preparation");
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status to PREPARING
        order.setOrderStatus(OrderEntity.OrderStatus.PREPARING);

        // Update estimated preparation time if provided
        if (estimatedTime != null) {
            order.setEstimatedPreparationTime(estimatedTime);
        }

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(OrderEntity.OrderStatus.PREPARING)
                .statusDescription(notes != null ? notes : "Order preparation started")
                .changedBy("vendor_" + vendorId)
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order preparation started successfully: {}", orderId);
        return convertToDto(savedOrder);
    }

    @Override
    public OrderDto markOrderReady(Long orderId, Integer actualTime, String notes, Long vendorId) {
        log.info("Marking order as ready: {} by vendor: {}", orderId, vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Verify order is in PREPARING status
        if (order.getOrderStatus() != OrderEntity.OrderStatus.PREPARING) {
            throw new RuntimeException("Order must be in PREPARING status to be marked as ready");
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status to READY
        order.setOrderStatus(OrderEntity.OrderStatus.READY);

        // Update actual preparation time if provided
        if (actualTime != null) {
            order.setActualPreparationTime(actualTime);
        }

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(OrderEntity.OrderStatus.READY)
                .statusDescription(notes != null ? notes : "Order is ready for pickup")
                .changedBy("vendor_" + vendorId)
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order marked as ready successfully: {}", orderId);
        return convertToDto(savedOrder);
    }

    @Override
    public OrderDto completeOrder(Long orderId, String notes, Long vendorId) {
        log.info("Completing order: {} by vendor: {}", orderId, vendorId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify the order belongs to the vendor
        if (!order.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Order does not belong to vendor: " + vendorId);
        }

        // Verify order is in READY status
        if (order.getOrderStatus() != OrderEntity.OrderStatus.READY) {
            throw new RuntimeException("Order must be in READY status to be completed");
        }

        // Store the previous status
        OrderEntity.OrderStatus previousStatus = order.getOrderStatus();

        // Update order status to COMPLETED
        order.setOrderStatus(OrderEntity.OrderStatus.COMPLETED);

        // Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // Create status history entry
        OrderStatusHistoryEntity statusHistory = OrderStatusHistoryEntity.builder()
                .order(savedOrder)
                .status(OrderEntity.OrderStatus.COMPLETED)
                .statusDescription(notes != null ? notes : "Order completed successfully")
                .changedBy("vendor_" + vendorId)
                .changedAt(LocalDateTime.now())
                .build();

        orderStatusHistoryRepository.save(statusHistory);

        log.info("Order completed successfully: {}", orderId);
        return convertToDto(savedOrder);
    }

    // Enhanced search and filter methods
    @Override
    public Page<OrderDto> searchOrders(String searchTerm, Long vendorId, Pageable pageable) {
        log.info("Searching orders for vendor: {} with term: {}", vendorId, searchTerm);

        // Use the existing search method from repository
        List<OrderEntity> orders = orderRepository.findBySearchTerm(searchTerm);

        // Filter by vendor and convert to DTOs
        List<OrderDto> filteredOrders = orders.stream()
                .filter(order -> order.getVendor().getId().equals(vendorId))
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // For simplicity, we'll return a simple page implementation
        // In a production environment, you might want to implement proper pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredOrders.size());

        return new org.springframework.data.domain.PageImpl<>(
                filteredOrders.subList(start, end),
                pageable,
                filteredOrders.size());
    }

    @Override
    public Page<OrderDto> filterOrders(OrderFilterRequestDto filterRequest, Long vendorId, Pageable pageable) {
        log.info("Filtering orders for vendor: {} with filters: {}", vendorId, filterRequest);

        // This is similar to getAllOrders but with more specific filtering
        // For now, we'll use the existing getAllOrders method
        return getAllOrders(filterRequest, vendorId);
    }
}