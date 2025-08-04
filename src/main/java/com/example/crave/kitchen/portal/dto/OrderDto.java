package com.example.crave.kitchen.portal.dto;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long vendorId;
    private String vendorName;
    private OrderEntity.OrderStatus orderStatus;
    private OrderEntity.OrderType orderType;
    private BigDecimal totalAmount;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal tipAmount;
    private BigDecimal discountAmount;
    private OrderEntity.PaymentStatus paymentStatus;
    private OrderEntity.PaymentMethod paymentMethod;
    private Integer estimatedPreparationTime;
    private Integer actualPreparationTime;
    private LocalDateTime pickupTime;
    private String tableNumber;
    private String orderNotes;
    private String customerNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDto> orderItems;
    private List<OrderPromotionDto> promotions;
    private List<OrderStatusHistoryDto> statusHistory;
}