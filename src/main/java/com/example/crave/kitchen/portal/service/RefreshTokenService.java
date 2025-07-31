package com.example.crave.kitchen.portal.service;


import com.example.crave.kitchen.portal.entity.RefreshToken;
import com.example.crave.kitchen.portal.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {



    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    /**
     * Generate refresh token for user
     */
    public RefreshToken generateRefreshToken(Long userId, String deviceInfo, String ipAddress, String userAgent) {
        try {
            // Revoke existing refresh tokens for this user
            revokeAllUserTokens(userId);

            // Create new refresh token
            RefreshToken refreshToken = RefreshToken.builder()
                    .userId(userId)
                    .token(UUID.randomUUID().toString())
                    .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                    .isRevoked(false)
                    .deviceInfo(deviceInfo)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            return refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            log.error("Failed to generate refresh token for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    /**
     * Validate refresh token
     */
    public Optional<RefreshToken> validateRefreshToken(String token) {
        try {
            return refreshTokenRepository.findByToken(token)
                    .filter(refreshToken -> !refreshToken.getIsRevoked())
                    .filter(refreshToken -> !refreshToken.getExpiresAt().isBefore(LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Failed to validate refresh token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Revoke refresh token
     */
    public void revokeRefreshToken(String token) {
        try {
            refreshTokenRepository.findByToken(token)
                    .ifPresent(refreshToken -> {
                        refreshToken.setIsRevoked(true);
                        refreshToken.setRevokedAt(LocalDateTime.now());
                        refreshTokenRepository.save(refreshToken);
                        log.info("Refresh token revoked for user: {}", refreshToken.getUserId());
                    });
        } catch (Exception e) {
            log.error("Failed to revoke refresh token: {}", e.getMessage());
        }
    }

    /**
     * Revoke all refresh tokens for a user
     */
    public void revokeAllUserTokens(Long userId) {
        try {
            refreshTokenRepository.findByUserIdAndIsRevokedFalse(userId)
                    .forEach(refreshToken -> {
                        refreshToken.setIsRevoked(true);
                        refreshToken.setRevokedAt(LocalDateTime.now());
                        refreshTokenRepository.save(refreshToken);
                    });
            log.info("All refresh tokens revoked for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to revoke all tokens for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Get refresh token by token string
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Get all active refresh tokens for a user
     */
    public java.util.List<RefreshToken> findActiveTokensByUserId(Long userId) {
        return refreshTokenRepository.findByUserIdAndIsRevokedFalse(userId);
    }

    /**
     * Delete expired refresh tokens
     */
    public void deleteExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            refreshTokenRepository.deleteByExpiresAtBefore(now);
            log.info("Expired refresh tokens cleaned up");
        } catch (Exception e) {
            log.error("Failed to delete expired tokens: {}", e.getMessage());
        }
    }

    /**
     * Check if user has active refresh tokens
     */
    public boolean hasActiveTokens(Long userId) {
        return refreshTokenRepository.existsByUserIdAndIsRevokedFalse(userId);
    }

    /**
     * Get token count for user
     */
    public long getTokenCountForUser(Long userId) {
        return refreshTokenRepository.countByUserIdAndIsRevokedFalse(userId);
    }

    /**
     * Update device info for refresh token
     */
    public void updateDeviceInfo(String token, String deviceInfo, String ipAddress, String userAgent) {
        try {
            refreshTokenRepository.findByToken(token)
                    .ifPresent(refreshToken -> {
                        refreshToken.setDeviceInfo(deviceInfo);
                        refreshToken.setIpAddress(ipAddress);
                        refreshToken.setUserAgent(userAgent);
                        refreshTokenRepository.save(refreshToken);
                    });
        } catch (Exception e) {
            log.error("Failed to update device info for token: {}", e.getMessage());
        }
    }

    /**
     * Get refresh token statistics
     */
    public Map<String, Object> getTokenStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalTokens", refreshTokenRepository.count());
            stats.put("activeTokens", refreshTokenRepository.countByIsRevokedFalse());
            stats.put("revokedTokens", refreshTokenRepository.countByIsRevokedTrue());
            stats.put("expiredTokens", refreshTokenRepository.countByExpiresAtBefore(LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Failed to get token statistics: {}", e.getMessage());
        }
        return stats;
    }
}