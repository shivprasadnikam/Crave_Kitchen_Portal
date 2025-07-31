package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegistrationDto {

    // User Information
    private String email;
    private String password;
    private String confirmPassword;
    private String name;

    // Restaurant Information
    private String restaurantName;
    private String phone;
    private String cuisineType;
    private String description;

    // Address Information
    private AddressDto address;

    // Business Hours
    private Map<String, BusinessHoursDto> businessHours;

    // Terms and Marketing
    private Boolean acceptTerms;
    private Boolean acceptMarketing;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country = "USA";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHoursDto {
        private Boolean isOpen;
        private String openTime;
        private String closeTime;
    }
}