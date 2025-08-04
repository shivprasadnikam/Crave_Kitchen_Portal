package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPromotionDto {

    private Long id;
    private Long orderId;
    private Long promotionId;
    private String promotionName;
    private String promotionDescription;
    private String promotionCode;
    private BigDecimal discountAmount;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private BigDecimal discountPercentage;
}