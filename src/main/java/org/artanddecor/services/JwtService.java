package org.artanddecor.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.artanddecor.dto.TokenPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service for token generation, validation, and management
 * Uses jjwt library version 0.11.5
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token.expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenExpiration;

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        logger.debug("Extracting username from JWT token");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate token pair (access token + refresh token)
     */
    public TokenPair generateTokenPair(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.info("Generating token pair for user: {}", userDetails.getUsername());
        
        String accessToken = generateAccessToken(extraClaims, userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        
        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000) // Convert to seconds
                .build();
    }

    /**
     * Generate access token
     */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.debug("Generating access token for user: {}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        logger.debug("Generating refresh token for user: {}", userDetails.getUsername());
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    /**
     * Build JWT token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        long currentTimeMillis = System.currentTimeMillis();
        
        try {
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(currentTimeMillis))
                    .setExpiration(new Date(currentTimeMillis + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Error building JWT token for user {}: {}", userDetails.getUsername(), e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            logger.debug("Token validation for user {}: {}", userDetails.getUsername(), isValid);
            return isValid;
        } catch (Exception e) {
            logger.warn("Token validation failed for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.debug("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if we can't parse
        }
    }

    /**
     * Extract expiration date from token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Get signing key for JWT
     */
    private Key getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error creating signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to create JWT signing key", e);
        }
    }

    /**
     * Get token expiration time as LocalDateTime
     */
    public LocalDateTime getTokenExpirationTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Check if token can be refreshed (not expired for too long)
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.debug("Cannot refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate refresh token and extract username
     */
    public String validateRefreshToken(String refreshToken) {
        try {
            String username = extractUsername(refreshToken);
            if (username != null && !isTokenExpired(refreshToken)) {
                logger.debug("Refresh token validated for user: {}", username);
                return username;
            } else {
                logger.warn("Invalid or expired refresh token for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error validating refresh token: {}", e.getMessage());
            return null;
        }
    }
}