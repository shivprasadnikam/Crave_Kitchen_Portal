package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUserId(Long userId);

    List<LoginHistory> findByUserIdOrderByLoginAtDesc(Long userId);

    List<LoginHistory> findByIsSuccessful(Boolean isSuccessful);

    List<LoginHistory> findByUserIdAndIsSuccessful(Long userId, Boolean isSuccessful);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.loginAt >= :since")
    List<LoginHistory> findRecentLoginsByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.isSuccessful = false AND lh.loginAt >= :since")
    List<LoginHistory> findRecentFailedLoginsByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.ipAddress = :ipAddress AND lh.isSuccessful = false AND lh.loginAt >= :since")
    List<LoginHistory> findRecentFailedLoginsByIp(@Param("ipAddress") String ipAddress,
            @Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId ORDER BY lh.loginAt DESC LIMIT 1")
    LoginHistory findLastLoginByUser(@Param("userId") Long userId);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.isSuccessful = true ORDER BY lh.loginAt DESC LIMIT 1")
    LoginHistory findLastSuccessfulLoginByUser(@Param("userId") Long userId);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.loginAt >= :since")
    List<LoginHistory> findLoginsSince(@Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.loginAt >= :since AND lh.isSuccessful = true")
    List<LoginHistory> findSuccessfulLoginsSince(@Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.loginAt >= :since AND lh.isSuccessful = false")
    List<LoginHistory> findFailedLoginsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.isSuccessful = false AND lh.loginAt >= :since")
    long countFailedLoginsByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.ipAddress = :ipAddress AND lh.isSuccessful = false AND lh.loginAt >= :since")
    long countFailedLoginsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.deviceType = :deviceType AND lh.loginAt >= :since")
    List<LoginHistory> findLoginsByDeviceTypeSince(@Param("deviceType") LoginHistory.DeviceType deviceType,
            @Param("since") LocalDateTime since);

    @Query("SELECT lh.ipAddress, COUNT(lh) FROM LoginHistory lh WHERE lh.isSuccessful = false AND lh.loginAt >= :since GROUP BY lh.ipAddress HAVING COUNT(lh) >= :threshold")
    List<Object[]> findSuspiciousIpAddresses(@Param("since") LocalDateTime since, @Param("threshold") long threshold);
}