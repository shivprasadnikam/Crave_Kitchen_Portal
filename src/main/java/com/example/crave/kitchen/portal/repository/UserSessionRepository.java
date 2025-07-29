package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionId(String sessionId);

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    List<UserSession> findByUserId(Long userId);

    List<UserSession> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT us FROM UserSession us WHERE us.sessionId = :sessionId AND us.isActive = true AND us.expiresAt > :now")
    Optional<UserSession> findValidSession(@Param("sessionId") String sessionId, @Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us WHERE us.refreshTokenHash = :refreshTokenHash AND us.isActive = true AND us.expiresAt > :now")
    Optional<UserSession> findValidSessionByRefreshToken(@Param("refreshTokenHash") String refreshTokenHash,
            @Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true AND us.expiresAt > :now")
    List<UserSession> findActiveSessionsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us WHERE us.lastActivityAt < :inactiveSince")
    List<UserSession> findInactiveSessions(@Param("inactiveSince") LocalDateTime inactiveSince);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true ORDER BY us.lastActivityAt DESC")
    List<UserSession> findActiveSessionsOrderedByActivity(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.user.id = :userId")
    void deactivateAllSessionsByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.sessionId = :sessionId")
    void deactivateSession(@Param("sessionId") String sessionId);

    @Modifying
    @Query("UPDATE UserSession us SET us.lastActivityAt = :now WHERE us.sessionId = :sessionId")
    void updateLastActivity(@Param("sessionId") String sessionId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true")
    long countActiveSessionsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.isActive = true AND us.expiresAt > :now")
    long countActiveSessions(@Param("now") LocalDateTime now);

    boolean existsBySessionId(String sessionId);

    boolean existsBySessionIdAndIsActiveTrue(String sessionId);
}