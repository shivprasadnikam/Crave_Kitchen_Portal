package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectOrderRequestDto {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    private String notes;
}