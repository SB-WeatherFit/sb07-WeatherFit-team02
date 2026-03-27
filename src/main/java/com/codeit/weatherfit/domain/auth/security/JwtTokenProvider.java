package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String ACCESS_SECRET_KEY = "temporary-weatherfit-access-secret-key-temporary-weatherfit-access-secret-key";
    private static final String REFRESH_SECRET_KEY = "temporary-weatherfit-refresh-secret-key-temporary-weatherfit-refresh-secret-key";
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 60L * 60L;
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 60L * 60L * 24L * 7L;

    private final SecretKey accessSecretKey = Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final SecretKey refreshSecretKey = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateAccessToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "ACCESS",
                accessSecretKey,
                ACCESS_TOKEN_EXPIRE_SECONDS
        );
    }

    public String generateRefreshToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "REFRESH",
                refreshSecretKey,
                REFRESH_TOKEN_EXPIRE_SECONDS
        );
    }

    public boolean isValidAccessToken(String token) {
        return isValidToken(token, accessSecretKey, "ACCESS");
    }

    public boolean isValidRefreshToken(String token) {
        return isValidToken(token, refreshSecretKey, "REFRESH");
    }

    public UUID getUserId(String token) {
        Claims claims = parseClaims(token, accessSecretKey);
        if (claims == null || claims.getSubject() == null) {
            return null;
        }

        try {
            return UUID.fromString(claims.getSubject());
        } catch (Exception exception) {
            return null;
        }
    }

    public String getEmail(String token) {
        Claims claims = parseClaims(token, accessSecretKey);
        if (claims == null) {
            return null;
        }

        return claims.get("email", String.class);
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token, accessSecretKey);
        if (claims == null) {
            return null;
        }

        return claims.get("role", String.class);
    }

    private boolean isValidToken(String token, SecretKey secretKey, String expectedTokenType) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }

            Claims claims = parseClaims(token, secretKey);
            if (claims == null) {
                return false;
            }

            String tokenType = claims.get("tokenType", String.class);
            return expectedTokenType.equals(tokenType);
        } catch (Exception exception) {
            return false;
        }
    }

    private String generateToken(
            UUID userId,
            String email,
            String role,
            String tokenType,
            SecretKey secretKey,
            long expireSeconds
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expireSeconds);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("tokenType", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token, SecretKey secretKey) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception exception) {
            return null;
        }
    }
}