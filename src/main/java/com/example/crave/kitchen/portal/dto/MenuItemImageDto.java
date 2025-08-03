package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemImageDto {

    private Long id;
    private Long menuItemId;
    private String imageUrl;
    private String imageType;
    private String fileName;
    private Long fileSizeBytes;
    private Integer widthPixels;
    private Integer heightPixels;
    private Boolean isPrimary;
    private Integer displayOrder;
    private String altText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}