package com.example.crave.kitchen.portal.util;

import com.example.crave.kitchen.portal.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Utility class to extract data from JWT tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenDataExtractor {

    private final JwtTokenService jwtTokenService;

    /**
     * Extract vendor ID from the current request's JWT token
     */
    public Long getCurrentVendorId() {
        // First try to get from request context
        String token = extractTokenFromRequest();
        log.debug("Extracted token from request: {}",
                token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "null");

        if (token != null) {
            Long vendorId = jwtTokenService.extractVendorId(token);
            log.debug("Extracted vendor ID from token: {}", vendorId);
            return vendorId;
        } else {
            log.warn("No token found in request context, trying alternative method");
            // Try alternative method using SecurityContext
            return getCurrentVendorIdFromSecurityContext();
        }
    }

    /**
     * Alternative method to get vendor ID from SecurityContext
     */
    private Long getCurrentVendorIdFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                log.debug("Authentication found: {}", authentication.getName());

                // For now, we can't get vendor ID from SecurityContext without
                // CustomAuthenticationDetails
                // This method is kept for future enhancement if needed
                log.debug("Vendor ID not available from SecurityContext - using fallback to database lookup");
                return null;
            } else {
                log.warn("No valid authentication found in SecurityContext");
                return null;
            }
        } catch (Exception e) {
            log.error("Error getting vendor ID from SecurityContext: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract email from the current request's JWT token
     */
    public String getCurrentUserEmail() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtTokenService.extractEmail(token);
        }
        return null;
    }

    /**
     * Extract all claims from the current request's JWT token
     */
    public Map<String, Object> getAllTokenData() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtTokenService.extractAllClaimsAsMap(token);
        }
        return null;
    }

    /**
     * Extract specific claim from the current request's JWT token
     */
    public <T> T getClaim(String key, Class<T> clazz) {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtTokenService.extractClaimByKey(token, key, clazz);
        }
        return null;
    }

    /**
     * Get token expiration date
     */
    public Date getTokenExpiration() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtTokenService.extractExpiration(token);
        }
        return null;
    }

    /**
     * Check if token is expiring soon (within specified minutes)
     */
    public boolean isTokenExpiringSoon(int minutesThreshold) {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtTokenService.isTokenExpiringSoon(token, minutesThreshold);
        }
        return false;
    }

    /**
     * Extract JWT token from the current request
     */
    private String extractTokenFromRequest() {
        try {
            // Check if we have request context
            if (RequestContextHolder.getRequestAttributes() == null) {
                log.warn("No request context found - RequestContextHolder.getRequestAttributes() is null");
                return null;
            }

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();

            if (request == null) {
                log.warn("HttpServletRequest is null");
                return null;
            }

            String authHeader = request.getHeader("Authorization");
            log.debug("Authorization header: {}",
                    authHeader != null ? authHeader.substring(0, Math.min(50, authHeader.length())) + "..." : "null");

            if (authHeader != null) {
                if (authHeader.startsWith("Bearer ")) {
                    // Standard format: "Bearer <token>"
                    String token = authHeader.substring(7);
                    log.debug("Successfully extracted token from Bearer Authorization header");
                    return token;
                } else {
                    // Direct token format: just the token
                    log.debug("Authorization header contains direct token (no Bearer prefix)");
                    return authHeader;
                }
            } else {
                log.warn("Authorization header is null");
            }
        } catch (Exception e) {
            log.warn("Failed to extract token from request: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get current authentication info for debugging
     */
    public void logCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.debug("Current user: {}", authentication.getName());
            log.debug("Authorities: {}", authentication.getAuthorities());

            String token = extractTokenFromRequest();
            if (token != null) {
                Long vendorId = jwtTokenService.extractVendorId(token);
                log.debug("Vendor ID from token: {}", vendorId);
            }
        } else {
            log.warn("No authentication found");
        }
    }
}