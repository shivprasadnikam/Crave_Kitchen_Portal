package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.dto.VendorRegistrationDto;
import com.example.crave.kitchen.portal.dto.VendorRegistrationResponseDto;

public interface VendorRegistrationService {

    VendorRegistrationResponseDto registerVendor(VendorRegistrationDto registrationDto);
    boolean validateRegistrationData(VendorRegistrationDto registrationDto);
}