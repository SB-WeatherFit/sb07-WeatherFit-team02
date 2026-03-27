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

    private static final String ACCESS_SECRET_KEY = "temporary-weatherfit-access-secret-key-temporary-weatherfit-access-secret-key"; //수정
    private static final String REFRESH_SECRET_KEY = "temporary-weatherfit-refresh-secret-key-temporary-weatherfit-refresh-secret-key"; //수정
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 60L * 60L;
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 60L * 60L * 24L * 7L;

    private final SecretKey accessSecretKey = Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes(StandardCharsets.UTF_8)); //수정
    private final SecretKey refreshSecretKey = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)); //수정

    public String generateAccessToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "ACCESS",
                accessSecretKey, //수정
                ACCESS_TOKEN_EXPIRE_SECONDS
        );
    }

    public String generateRefreshToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "REFRESH",
                refreshSecretKey, //수정
                REFRESH_TOKEN_EXPIRE_SECONDS
        );
    }

    public boolean isValidAccessToken(String token) {
        return isValidToken(token, accessSecretKey, "ACCESS"); //수정
    }

    public boolean isValidRefreshToken(String token) {
        return isValidToken(token, refreshSecretKey, "REFRESH"); //수정
    }

    public UUID getUserId(String token) {
        Claims claims = parseClaims(token, accessSecretKey); //수정
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
        Claims claims = parseClaims(token, accessSecretKey); //수정
        if (claims == null) {
            return null;
        }

        return claims.get("email", String.class);
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token, accessSecretKey); //수정
        if (claims == null) {
            return null;
        }

        return claims.get("role", String.class);
    }

    private boolean isValidToken(String token, SecretKey secretKey, String expectedTokenType) { //수정
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
            SecretKey secretKey, //수정
            long expireSeconds
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expireSeconds);

        return Jwts.builder() //수정
                .subject(userId.toString()) //수정
                .claim("email", email) //수정
                .claim("role", role) //수정
                .claim("tokenType", tokenType) //수정
                .issuedAt(Date.from(now)) //수정
                .expiration(Date.from(expiresAt)) //수정
                .signWith(secretKey) //수정
                .compact(); //수정
    }

    private Claims parseClaims(String token, SecretKey secretKey) { //수정
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