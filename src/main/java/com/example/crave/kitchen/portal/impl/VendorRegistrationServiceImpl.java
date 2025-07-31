package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.dto.ApiResponseDto;
import com.example.crave.kitchen.portal.dto.VendorRegistrationDto;
import com.example.crave.kitchen.portal.dto.VendorRegistrationResponseDto;
import com.example.crave.kitchen.portal.entity.BusinessHoursEntity;
import com.example.crave.kitchen.portal.entity.VendorsEntity;
import com.example.crave.kitchen.portal.entity.VendorProfileEntity;
import com.example.crave.kitchen.portal.repository.BusinessHoursRepository;
import com.example.crave.kitchen.portal.repository.VendorProfileRepository;
import com.example.crave.kitchen.portal.repository.VendorRepository;
import com.example.crave.kitchen.portal.service.VendorRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorRegistrationServiceImpl implements VendorRegistrationService {

    private final VendorRepository vendorRepository;

    private final VendorProfileRepository vendorProfileRepository;

    private final BusinessHoursRepository businessHoursRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public VendorRegistrationResponseDto registerVendor(VendorRegistrationDto dto) {

        // Create Vendor
        VendorsEntity vendorsEntity = createUser(dto);
        // Create vendor profile
        VendorProfileEntity vendorProfileEntity = createVendorProfile(dto, vendorsEntity);
        // Create business hours
        List<BusinessHoursEntity> businessHoursEntityList = createBusinessHours(dto, vendorProfileEntity);
        vendorRepository.save(vendorsEntity);

        // Build response
        return buildRegistrationResponse(vendorsEntity, vendorProfileEntity, dto.getBusinessHours(), "accessToken",
                "refreshToken");
    }

    @Override
    public boolean validateRegistrationData(VendorRegistrationDto registrationDto) {
        return false;
    }

    private VendorsEntity createUser(VendorRegistrationDto dto) {
        VendorsEntity vendorsEntity = new VendorsEntity();
        vendorsEntity.setEmail(dto.getEmail());
        vendorsEntity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        vendorsEntity.setFullName(dto.getName());
        vendorsEntity.setPhone(dto.getPhone());
        vendorsEntity.setIsActive(false);
        vendorsEntity.setIsEmailVerified(false);

        return vendorRepository.save(vendorsEntity);
    }

    private VendorProfileEntity createVendorProfile(VendorRegistrationDto dto, VendorsEntity vendorsEntity) {
        VendorProfileEntity vendorProfileEntity = new VendorProfileEntity();
        vendorProfileEntity.setVendorsEntity(vendorsEntity);
        vendorProfileEntity.setRestaurantName(dto.getRestaurantName());
        vendorProfileEntity.setCuisineType(dto.getCuisineType());
        vendorProfileEntity.setDescription(dto.getDescription());
        vendorProfileEntity.setStreetAddress(dto.getAddress().getStreet());
        vendorProfileEntity.setCity(dto.getAddress().getCity());
        vendorProfileEntity.setState(dto.getAddress().getState());
        vendorProfileEntity.setZipCode(dto.getAddress().getZipCode());
        vendorProfileEntity.setCountry(dto.getAddress().getCountry());
        vendorProfileEntity.setPhone(dto.getPhone());
        vendorProfileEntity.setIsApproved(true);
        vendorProfileEntity.setApprovalStatus(VendorProfileEntity.ApprovalStatus.PENDING);

        return vendorProfileRepository.save(vendorProfileEntity);
    }

    private List<BusinessHoursEntity> createBusinessHours(VendorRegistrationDto dto,
            VendorProfileEntity vendorProfileEntity) {
        List<BusinessHoursEntity> businessHoursEntityList = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Map.Entry<String, VendorRegistrationDto.BusinessHoursDto> entry : dto.getBusinessHours().entrySet()) {
            String day = entry.getKey();
            VendorRegistrationDto.BusinessHoursDto hoursDto = entry.getValue();

            BusinessHoursEntity businessHoursEntity = new BusinessHoursEntity();
            businessHoursEntity.setVendor(vendorProfileEntity);
            businessHoursEntity.setDayOfWeek(BusinessHoursEntity.DayOfWeek.valueOf(day.toUpperCase()));
            businessHoursEntity.setIsOpen(hoursDto.getIsOpen());

            if (hoursDto.getIsOpen()) {
                businessHoursEntity.setOpenTime(LocalTime.parse(hoursDto.getOpenTime(), timeFormatter));
                businessHoursEntity.setCloseTime(LocalTime.parse(hoursDto.getCloseTime(), timeFormatter));
            }

            businessHoursEntityList.add(businessHoursRepository.save(businessHoursEntity));
        }

        return businessHoursEntityList;
    }

    private VendorRegistrationResponseDto buildRegistrationResponse(VendorsEntity vendorsEntity,
            VendorProfileEntity vendorProfileEntity,
            Map<String, VendorRegistrationDto.BusinessHoursDto> businessHoursDto,
            String accessToken, String refreshToken) {

        // Build user DTO
        VendorRegistrationResponseDto.UserDto userDto = VendorRegistrationResponseDto.UserDto.builder()
                .id(vendorsEntity.getId())
                .email(vendorsEntity.getEmail())
                .name(vendorsEntity.getFullName())
                .isActive(vendorsEntity.getIsActive())
                .isEmailVerified(vendorsEntity.getIsEmailVerified())
                .createdAt(vendorsEntity.getCreatedAt())
                .updatedAt(vendorsEntity.getUpdatedAt())
                .build();

        // Build vendor profile DTO
        VendorRegistrationResponseDto.VendorProfileDto vendorProfileDto = VendorRegistrationResponseDto.VendorProfileDto
                .builder()
                .id(vendorProfileEntity.getId())
                .restaurantName(vendorProfileEntity.getRestaurantName())
                .cuisineType(vendorProfileEntity.getCuisineType())
                .description(vendorProfileEntity.getDescription())
                .address(VendorRegistrationResponseDto.AddressDto.builder()
                        .street(vendorProfileEntity.getStreetAddress())
                        .city(vendorProfileEntity.getCity())
                        .state(vendorProfileEntity.getState())
                        .zipCode(vendorProfileEntity.getZipCode())
                        .country(vendorProfileEntity.getCountry())
                        .build())
                .phone(vendorProfileEntity.getPhone())
                .isApproved(vendorProfileEntity.getIsApproved())
                .approvalStatus(vendorProfileEntity.getApprovalStatus().name().toLowerCase())
                .createdAt(vendorProfileEntity.getCreatedAt())
                .build();

        // Build business hours DTO
        Map<String, VendorRegistrationResponseDto.BusinessHoursDto> businessHoursResponse = businessHoursDto.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> VendorRegistrationResponseDto.BusinessHoursDto.builder()
                                .isOpen(entry.getValue().getIsOpen())
                                .openTime(entry.getValue().getOpenTime())
                                .closeTime(entry.getValue().getCloseTime())
                                .build()));

        // Build tokens DTO
        VendorRegistrationResponseDto.TokenDto tokensDto = VendorRegistrationResponseDto.TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600) // 1 hour
                .build();

        // Build next steps
        List<String> nextSteps = Arrays.asList(
                "Check your email for verification link",
                "Complete email verification to activate account",
                "Wait for admin approval (usually within 24 hours)",
                "Set up your menu and pricing once approved");

        return VendorRegistrationResponseDto.builder()
                .user(userDto)
                .vendorProfile(vendorProfileDto)
                .businessHours(businessHoursResponse)
                .tokens(tokensDto)
                .nextSteps(nextSteps)
                .build();
    }

    // Custom exceptions
    public static class ValidationException extends RuntimeException {
        private final List<ApiResponseDto.ValidationErrorDto> errors;

        public ValidationException(String message, List<ApiResponseDto.ValidationErrorDto> errors) {
            super(message);
            this.errors = errors;
        }

        public List<ApiResponseDto.ValidationErrorDto> getErrors() {
            return errors;
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class RestaurantNameTakenException extends RuntimeException {
        public RestaurantNameTakenException(String message) {
            super(message);
        }
    }

}