package com.example.crave.kitchen.portal.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate access token for user
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenExpiration * 1000);
    }

    /**
     * Generate access token for user with vendor information
     */
    public String generateAccessToken(UserDetails userDetails, Long vendorId) {
        return generateToken(userDetails, vendorId, accessTokenExpiration * 1000);
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration * 1000);
    }

    /**
     * Generate refresh token for user with vendor information
     */
    public String generateRefreshToken(UserDetails userDetails, Long vendorId) {
        return generateToken(userDetails, vendorId, refreshTokenExpiration * 1000);
    }

    /**
     * Generate token with custom claims
     */
    public String generateToken(UserDetails userDetails, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("enabled", userDetails.isEnabled());
        claims.put("accountNonExpired", userDetails.isAccountNonExpired());
        claims.put("accountNonLocked", userDetails.isAccountNonLocked());
        claims.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());

        return createToken(claims, userDetails.getUsername(), expiration);
    }

    /**
     * Generate token with custom claims including vendor information
     */
    public String generateToken(UserDetails userDetails, Long vendorId, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("enabled", userDetails.isEnabled());
        claims.put("accountNonExpired", userDetails.isAccountNonExpired());
        claims.put("accountNonLocked", userDetails.isAccountNonLocked());
        claims.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
        claims.put("vendorId", vendorId);

        return createToken(claims, userDetails.getUsername(), expiration);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer("crave-kitchen-portal")
                .setAudience("vendors")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract email from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(30) // Allow 30 seconds clock skew
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            logger.error("JWT token signature validation failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT token is empty or null: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validate token for user
     */
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (email.equals(tokenEmail) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Token validation failed for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Validate token without user email (for general validation)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get token expiration time in milliseconds
     */
    public Long getTokenExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime();
        } catch (Exception e) {
            logger.error("Failed to get token expiration time: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if token will expire soon (within specified minutes)
     */
    public Boolean isTokenExpiringSoon(String token, int minutesThreshold) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            long timeUntilExpiration = expiration.getTime() - now.getTime();
            long thresholdMs = minutesThreshold * 60 * 1000L;
            return timeUntilExpiration <= thresholdMs;
        } catch (Exception e) {
            logger.error("Failed to check if token is expiring soon: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Generate token pair (access + refresh)
     */
    public Map<String, String> generateTokenPair(UserDetails userDetails) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(userDetails));
        tokens.put("refreshToken", generateRefreshToken(userDetails));
        return tokens;
    }

    /**
     * Extract user ID from token (if present in claims)
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            logger.error("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract vendor ID from token
     */
    public Long extractVendorId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            logger.debug("Extracted claims: {}", claims);

            Object vendorIdObj = claims.get("vendorId");
            logger.debug("Vendor ID object from claims: {} (type: {})", vendorIdObj,
                    vendorIdObj != null ? vendorIdObj.getClass().getSimpleName() : "null");

            Long vendorId = claims.get("vendorId", Long.class);
            logger.debug("Extracted vendor ID: {}", vendorId);
            return vendorId;
        } catch (Exception e) {
            logger.error("Failed to extract vendor ID from token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract authorities from token
     */
    public String extractAuthorities(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("authorities", String.class);
        } catch (Exception e) {
            logger.error("Failed to extract authorities from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract all claims from token as a Map
     */
    public Map<String, Object> extractAllClaimsAsMap(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> claimsMap = new HashMap<>();
            claimsMap.put("email", claims.getSubject());
            claimsMap.put("vendorId", claims.get("vendorId"));
            claimsMap.put("authorities", claims.get("authorities"));
            claimsMap.put("enabled", claims.get("enabled"));
            claimsMap.put("accountNonExpired", claims.get("accountNonExpired"));
            claimsMap.put("accountNonLocked", claims.get("accountNonLocked"));
            claimsMap.put("credentialsNonExpired", claims.get("credentialsNonExpired"));
            claimsMap.put("issuedAt", claims.getIssuedAt());
            claimsMap.put("expiration", claims.getExpiration());
            return claimsMap;
        } catch (Exception e) {
            logger.error("Failed to extract all claims from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract specific claim by key
     */
    public <T> T extractClaimByKey(String token, String key, Class<T> clazz) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get(key, clazz);
        } catch (Exception e) {
            logger.error("Failed to extract claim '{}' from token: {}", key, e.getMessage());
            return null;
        }
    }
}