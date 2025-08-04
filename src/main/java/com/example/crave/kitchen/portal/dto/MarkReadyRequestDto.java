package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkReadyRequestDto {

    private String notes;
    private Integer actualPreparationTime;
}