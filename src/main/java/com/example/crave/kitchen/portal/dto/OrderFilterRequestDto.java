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
public class OrderFilterRequestDto {

    private OrderEntity.OrderStatus orderStatus;
    private OrderEntity.PaymentStatus paymentStatus;
    private OrderEntity.OrderType orderType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String searchTerm;
    private String orderNumber;
    private Long customerId;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}