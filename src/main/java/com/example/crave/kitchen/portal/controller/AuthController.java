package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.ApiResponseDto;
import com.example.crave.kitchen.portal.entity.VendorsEntity;
import com.example.crave.kitchen.portal.repository.VendorRepository;
import com.example.crave.kitchen.portal.service.CustomUserDetailsService;
import com.example.crave.kitchen.portal.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private VendorRepository vendorRepository;

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();



            // Get vendor information
            VendorsEntity vendor = vendorRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Generate tokens with vendor information
            String accessToken = jwtTokenService.generateAccessToken(userDetails, vendor.getId());
            String refreshToken = jwtTokenService.generateRefreshToken(userDetails, vendor.getId());

            // Prepare response
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", accessToken);
            data.put("refreshToken", refreshToken);
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 3600); // 1 hour
            data.put("vendorId", vendor.getId());
            data.put("vendorEmail", vendor.getEmail());
            data.put("vendorName", vendor.getFullName());
            data.put("user", userDetails);

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .success(true)
                    .message("Login successful")
                    .data(data)
                    .build());

        } catch (BadCredentialsException e) {
            logger.warn("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Invalid email or password")
                            .build());
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Login failed")
                            .build());
        }
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) {
        try {
            // Validate refresh token
            if (!jwtTokenService.validateToken(refreshRequest.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.builder()
                                .success(false)
                                .message("Invalid refresh token")
                                .build());
            }

            // Extract email from refresh token
            String email = jwtTokenService.extractEmail(refreshRequest.getRefreshToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);



            // Get vendor information
            VendorsEntity vendor = vendorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Generate new access token with vendor information
            String newAccessToken = jwtTokenService.generateAccessToken(userDetails, vendor.getId());

            // Prepare response
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 3600);
            data.put("vendorId", vendor.getId());
            data.put("vendorEmail", vendor.getEmail());
            data.put("vendorName", vendor.getFullName());

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(data)
                    .build());

        } catch (Exception e) {
            logger.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Token refresh failed")
                            .build());
        }
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // In a real implementation, you would blacklist the token
                logger.info("User logged out successfully");
            }

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .success(true)
                    .message("Logout successful")
                    .build());

        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Logout failed")
                            .build());
        }
    }

    /**
     * Validate token endpoint
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponseDto> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.builder()
                                .success(false)
                                .message("Invalid token format")
                                .build());
            }

            String token = authHeader.substring(7);
            String email = jwtTokenService.extractEmail(token);

            if (jwtTokenService.validateToken(token, email)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Get vendor information
                VendorsEntity vendor = vendorRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Vendor not found"));

                Map<String, Object> data = new HashMap<>();
                data.put("valid", true);
                data.put("email", email);
                data.put("vendorId", vendor.getId());
                data.put("vendorName", vendor.getFullName());
                data.put("authorities", userDetails.getAuthorities());

                return ResponseEntity.ok(ApiResponseDto.builder()
                        .success(true)
                        .message("Token is valid")
                        .data(data)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.builder()
                                .success(false)
                                .message("Token is invalid")
                                .build());
            }

        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Token validation failed")
                            .build());
        }
    }

    /**
     * Get token info endpoint
     */
    @GetMapping("/token-info")
    public ResponseEntity<ApiResponseDto> getTokenInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.builder()
                                .success(false)
                                .message("Invalid token format")
                                .build());
            }

            String token = authHeader.substring(7);
            String email = jwtTokenService.extractEmail(token);
            Date expiration = jwtTokenService.extractExpiration(token);
            Long userId = jwtTokenService.extractUserId(token);

            // Get vendor information
            VendorsEntity vendor = vendorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("userId", userId);
            data.put("vendorId", vendor.getId());
            data.put("vendorName", vendor.getFullName());
            data.put("expiration", expiration);
            data.put("expiresIn", jwtTokenService.getTokenExpirationTime(token));
            data.put("isExpiringSoon", jwtTokenService.isTokenExpiringSoon(token, 30)); // 30 minutes

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .success(true)
                    .message("Token information retrieved")
                    .data(data)
                    .build());

        } catch (Exception e) {
            logger.error("Token info error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.builder()
                            .success(false)
                            .message("Failed to get token information")
                            .build());
        }
    }

    // Request DTOs
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        // Getters and setters
        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}