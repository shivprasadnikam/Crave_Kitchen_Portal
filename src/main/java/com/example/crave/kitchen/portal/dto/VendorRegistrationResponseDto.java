package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorRegistrationResponseDto {

    private UserDto user;
    private VendorProfileDto vendorProfile;
    private Map<String, BusinessHoursDto> businessHours;
    private TokenDto tokens;
    private List<String> nextSteps;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDto {
        private Long id;
        private String email;
        private String name;
        private String role;
        private Boolean isActive;
        private Boolean isEmailVerified;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VendorProfileDto {
        private Long id;
        private String restaurantName;
        private String cuisineType;
        private String description;
        private AddressDto address;
        private String phone;
        private Boolean isApproved;
        private String approvalStatus;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDto {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessHoursDto {
        private Boolean isOpen;
        private String openTime;
        private String closeTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;
        private Integer expiresIn;
    }
}