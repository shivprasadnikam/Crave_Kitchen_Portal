package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenHash(String tokenHash);

    List<AuthToken> findByUserId(Long userId);

    List<AuthToken> findByUserIdAndTokenType(Long userId, AuthToken.TokenType tokenType);

    List<AuthToken> findByUserIdAndIsRevokedFalse(Long userId);

    @Query("SELECT at FROM AuthToken at WHERE at.tokenHash = :tokenHash AND at.isRevoked = false AND at.expiresAt > :now")
    Optional<AuthToken> findValidToken(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    @Query("SELECT at FROM AuthToken at WHERE at.user.id = :userId AND at.tokenType = :tokenType AND at.isRevoked = false AND at.expiresAt > :now")
    List<AuthToken> findValidTokensByUserAndType(@Param("userId") Long userId,
            @Param("tokenType") AuthToken.TokenType tokenType, @Param("now") LocalDateTime now);

    @Query("SELECT at FROM AuthToken at WHERE at.expiresAt < :now")
    List<AuthToken> findExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT at FROM AuthToken at WHERE at.user.id = :userId AND at.isRevoked = false")
    List<AuthToken> findActiveTokensByUser(@Param("userId") Long userId);

    @Query("SELECT at FROM AuthToken at WHERE at.tokenType = :tokenType AND at.isRevoked = false AND at.expiresAt > :now")
    List<AuthToken> findValidTokensByType(@Param("tokenType") AuthToken.TokenType tokenType,
            @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE AuthToken at SET at.isRevoked = true WHERE at.user.id = :userId AND at.tokenType = :tokenType")
    void revokeAllTokensByUserAndType(@Param("userId") Long userId, @Param("tokenType") AuthToken.TokenType tokenType);

    @Modifying
    @Query("UPDATE AuthToken at SET at.isRevoked = true WHERE at.user.id = :userId")
    void revokeAllTokensByUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM AuthToken at WHERE at.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(at) FROM AuthToken at WHERE at.user.id = :userId AND at.tokenType = :tokenType AND at.isRevoked = false")
    long countActiveTokensByUserAndType(@Param("userId") Long userId,
            @Param("tokenType") AuthToken.TokenType tokenType);

    boolean existsByTokenHash(String tokenHash);

    boolean existsByTokenHashAndIsRevokedFalse(String tokenHash);
}