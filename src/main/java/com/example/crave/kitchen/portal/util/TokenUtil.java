package com.example.crave.kitchen.portal.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class TokenUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Extract token from Authorization header
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Validate token format (basic validation)
     */
    public boolean isValidTokenFormat(String token) {
        return token != null && !token.trim().isEmpty() && token.split("\\.").length == 3;
    }

    /**
     * Generate random token string
     */
    public String generateRandomToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate secure random string
     */
    public String generateSecureRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    /**
     * Mask sensitive data for logging
     */
    public String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(email.lastIndexOf('@'));
    }

    /**
     * Mask token for logging
     */
    public String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

    /**
     * Validate token expiration time
     */
    public boolean isTokenExpiringSoon(long expirationTime, int minutesThreshold) {
        long currentTime = System.currentTimeMillis();
        long timeUntilExpiration = expirationTime - currentTime;
        long thresholdMs = minutesThreshold * 60 * 1000L;
        return timeUntilExpiration <= thresholdMs;
    }

    /**
     * Get token expiration time in seconds
     */
    public long getTokenExpirationInSeconds(long expirationTime) {
        long currentTime = System.currentTimeMillis();
        return Math.max(0, (expirationTime - currentTime) / 1000);
    }
}