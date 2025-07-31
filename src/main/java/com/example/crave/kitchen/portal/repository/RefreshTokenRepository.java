package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token string
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all active refresh tokens for a user
     */
    List<RefreshToken> findByUserIdAndIsRevokedFalse(Long userId);

    /**
     * Find all revoked refresh tokens for a user
     */
    List<RefreshToken> findByUserIdAndIsRevokedTrue(Long userId);

    /**
     * Check if user has active refresh tokens
     */
    boolean existsByUserIdAndIsRevokedFalse(Long userId);

    /**
     * Count active refresh tokens for a user
     */
    long countByUserIdAndIsRevokedFalse(Long userId);

    /**
     * Count revoked refresh tokens
     */
    long countByIsRevokedTrue();

    /**
     * Count active refresh tokens
     */
    long countByIsRevokedFalse();

    /**
     * Find refresh tokens by user ID
     */
    List<RefreshToken> findByUserId(Long userId);

    /**
     * Find refresh tokens by device info
     */
    List<RefreshToken> findByDeviceInfo(String deviceInfo);

    /**
     * Find refresh tokens by IP address
     */
    List<RefreshToken> findByIpAddress(String ipAddress);

    /**
     * Find refresh tokens that expire before a specific date
     */
    List<RefreshToken> findByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * Count refresh tokens that expire before a specific date
     */
    long countByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * Find refresh tokens by user ID and device info
     */
    List<RefreshToken> findByUserIdAndDeviceInfo(Long userId, String deviceInfo);

    /**
     * Find refresh tokens by user ID and IP address
     */
    List<RefreshToken> findByUserIdAndIpAddress(Long userId, String ipAddress);

    /**
     * Find refresh tokens created after a specific date
     */
    List<RefreshToken> findByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * Find refresh tokens created between two dates
     */
    List<RefreshToken> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete expired refresh tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :dateTime")
    void deleteByExpiresAtBefore(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Delete all refresh tokens for a user
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * Revoke all refresh tokens for a user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, rt.revokedAt = :revokedAt WHERE rt.userId = :userId AND rt.isRevoked = false")
    void revokeAllUserTokens(@Param("userId") Long userId, @Param("revokedAt") LocalDateTime revokedAt);

    /**
     * Revoke refresh token by token string
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, rt.revokedAt = :revokedAt WHERE rt.token = :token")
    void revokeToken(@Param("token") String token, @Param("revokedAt") LocalDateTime revokedAt);

    /**
     * Update device info for a refresh token
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.deviceInfo = :deviceInfo, rt.ipAddress = :ipAddress, rt.userAgent = :userAgent WHERE rt.token = :token")
    void updateDeviceInfo(@Param("token") String token,
            @Param("deviceInfo") String deviceInfo,
            @Param("ipAddress") String ipAddress,
            @Param("userAgent") String userAgent);

    /**
     * Find refresh tokens by user agent
     */
    List<RefreshToken> findByUserAgent(String userAgent);

    /**
     * Find refresh tokens by user ID and user agent
     */
    List<RefreshToken> findByUserIdAndUserAgent(Long userId, String userAgent);

    /**
     * Count refresh tokens by user ID
     */
    long countByUserId(Long userId);

    /**
     * Find the most recent refresh token for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId ORDER BY rt.createdAt DESC")
    List<RefreshToken> findMostRecentByUserId(@Param("userId") Long userId);

    /**
     * Find refresh tokens that will expire soon (within specified minutes)
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt BETWEEN :now AND :expiryThreshold AND rt.isRevoked = false")
    List<RefreshToken> findTokensExpiringSoon(@Param("now") LocalDateTime now,
            @Param("expiryThreshold") LocalDateTime expiryThreshold);

    /**
     * Find active refresh tokens created in the last N days
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.createdAt >= :startDate AND rt.isRevoked = false")
    List<RefreshToken> findActiveTokensCreatedAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Get refresh token statistics
     */
    @Query("SELECT COUNT(rt) as total, " +
            "SUM(CASE WHEN rt.isRevoked = false THEN 1 ELSE 0 END) as active, " +
            "SUM(CASE WHEN rt.isRevoked = true THEN 1 ELSE 0 END) as revoked, " +
            "SUM(CASE WHEN rt.expiresAt < :now THEN 1 ELSE 0 END) as expired " +
            "FROM RefreshToken rt")
    Object[] getTokenStatistics(@Param("now") LocalDateTime now);
}