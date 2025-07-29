package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    List<PasswordResetToken> findByUserId(Long userId);

    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.tokenHash = :tokenHash AND prt.isUsed = false AND prt.expiresAt > :now")
    Optional<PasswordResetToken> findValidToken(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user.id = :userId AND prt.isUsed = false AND prt.expiresAt > :now")
    List<PasswordResetToken> findValidTokensByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    List<PasswordResetToken> findExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user.id = :userId AND prt.isUsed = false")
    List<PasswordResetToken> findUnusedTokensByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.isUsed = true WHERE prt.user.id = :userId")
    void markAllTokensAsUsedByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.isUsed = true WHERE prt.tokenHash = :tokenHash")
    void markTokenAsUsed(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(prt) FROM PasswordResetToken prt WHERE prt.user.id = :userId AND prt.isUsed = false AND prt.expiresAt > :now")
    long countValidTokensByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    boolean existsByTokenHash(String tokenHash);

    boolean existsByTokenHashAndIsUsedFalse(String tokenHash);
}