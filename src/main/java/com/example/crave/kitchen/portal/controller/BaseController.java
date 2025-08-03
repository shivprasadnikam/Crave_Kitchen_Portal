package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.util.TokenDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base controller that provides common functionality for all controllers
 */
@Slf4j
public abstract class BaseController {

    @Autowired
    protected TokenDataExtractor tokenDataExtractor;

    /**
     * Get current vendor ID from JWT token
     * 
     * @return vendor ID or null if not found
     */
    protected Long getCurrentVendorId() {
        Long vendorId = tokenDataExtractor.getCurrentVendorId();
        if (vendorId != null) {
            log.debug("Extracted vendor ID from token: {}", vendorId);
        } else {
            log.warn("Could not extract vendor ID from token");
        }
        return vendorId;
    }

    /**
     * Get current vendor ID from JWT token with exception if not found
     * 
     * @return vendor ID
     * @throws RuntimeException if vendor ID cannot be extracted
     */
    protected Long getCurrentVendorIdRequired() {
        Long vendorId = getCurrentVendorId();
        if (vendorId == null) {
            throw new RuntimeException("Vendor ID not found in token");
        }
        return vendorId;
    }

    /**
     * Get current user email from JWT token
     * 
     * @return user email or null if not found
     */
    protected String getCurrentUserEmail() {
        return tokenDataExtractor.getCurrentUserEmail();
    }
}