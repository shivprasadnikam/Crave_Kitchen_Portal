package com.example.crave.kitchen.portal.dto;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Order status is required")
    private OrderEntity.OrderStatus orderStatus;

    private String notes;
    private Integer estimatedPreparationTime;
    private Integer actualPreparationTime;
}