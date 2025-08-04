package com.example.crave.kitchen.portal.dto;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryDto {

    private Long id;
    private Long orderId;
    private OrderEntity.OrderStatus fromStatus;
    private OrderEntity.OrderStatus toStatus;
    private String notes;
    private String updatedBy;
    private LocalDateTime createdAt;
}