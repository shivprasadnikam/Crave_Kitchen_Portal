package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.ApiResponseDto;
import com.example.crave.kitchen.portal.dto.VendorRegistrationDto;
import com.example.crave.kitchen.portal.dto.VendorRegistrationResponseDto;
import com.example.crave.kitchen.portal.impl.VendorRegistrationServiceImpl;
import com.example.crave.kitchen.portal.service.VendorRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class VendorRegistrationController {

    @Autowired
    private VendorRegistrationService vendorRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<VendorRegistrationResponseDto>> registerVendor(
            @RequestBody VendorRegistrationDto registrationDto) {

        try {
            VendorRegistrationResponseDto response = vendorRegistrationService.registerVendor(registrationDto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(
                            "Registration successful. Please verify your email to activate your account.",
                            response));

        } catch (VendorRegistrationServiceImpl.ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.validationError("Validation failed", e.getErrors()));

        } catch (VendorRegistrationServiceImpl.EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage(), "EMAIL_ALREADY_EXISTS"));

        } catch (VendorRegistrationServiceImpl.RestaurantNameTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage(), "RESTAURANT_NAME_TAKEN"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error(
                            "Internal server error occurred during registration",
                            "INTERNAL_SERVER_ERROR"));
        }
    }

    @PostMapping("/register/validate")
    public ResponseEntity<ApiResponseDto<Object>> validateRegistrationData(
            @RequestBody VendorRegistrationDto registrationDto) {

        try {
            vendorRegistrationService.validateRegistrationData(registrationDto);
            return ResponseEntity.ok()
                    .body(ApiResponseDto.success("Validation successful", null));

        } catch (VendorRegistrationServiceImpl.ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.validationError("Validation failed", e.getErrors()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Validation failed", "VALIDATION_ERROR"));
        }
    }
}