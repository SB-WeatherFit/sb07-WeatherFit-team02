package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.user.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String ACCESS_SECRET_KEY = "temporary-weatherfit-access-secret-key";
    private static final String REFRESH_SECRET_KEY = "temporary-weatherfit-refresh-secret-key";
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 60L * 60L;
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 60L * 60L * 24L * 7L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateAccessToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "ACCESS",
                ACCESS_SECRET_KEY,
                ACCESS_TOKEN_EXPIRE_SECONDS
        );
    }

    public String generateRefreshToken(User user) {
        return generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                "REFRESH",
                REFRESH_SECRET_KEY,
                REFRESH_TOKEN_EXPIRE_SECONDS
        );
    }

    public boolean isValidAccessToken(String token) {
        return isValidToken(token, ACCESS_SECRET_KEY, "ACCESS");
    }

    public boolean isValidRefreshToken(String token) {
        return isValidToken(token, REFRESH_SECRET_KEY, "REFRESH");
    }

    public UUID getUserId(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload == null || payload.get("sub") == null) {
            return null;
        }

        try {
            return UUID.fromString(String.valueOf(payload.get("sub")));
        } catch (Exception exception) {
            return null;
        }
    }

    public String getEmail(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload == null || payload.get("email") == null) {
            return null;
        }

        return String.valueOf(payload.get("email"));
    }

    public String getRole(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload == null || payload.get("role") == null) {
            return null;
        }

        return String.valueOf(payload.get("role"));
    }

    private boolean isValidToken(String token, String secretKey, String expectedTokenType) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }

            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                return false;
            }

            String expectedSignature = sign(tokenParts[0] + "." + tokenParts[1], secretKey);
            if (!expectedSignature.equals(tokenParts[2])) {
                return false;
            }

            Map<String, Object> payload = parsePayload(token);
            if (payload == null) {
                return false;
            }

            Object tokenType = payload.get("tokenType");
            if (tokenType == null || !expectedTokenType.equals(String.valueOf(tokenType))) {
                return false;
            }

            Object expiration = payload.get("exp");
            if (expiration == null) {
                return false;
            }

            long expirationEpochSecond = Long.parseLong(String.valueOf(expiration));
            return expirationEpochSecond > Instant.now().getEpochSecond();
        } catch (Exception exception) {
            return false;
        }
    }

    private String generateToken(
            UUID userId,
            String email,
            String role,
            String tokenType,
            String secretKey,
            long expireSeconds
    ) {
        try {
            Instant now = Instant.now();
            Instant expiresAt = now.plusSeconds(expireSeconds);

            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = "{"
                    + "\"sub\":\"" + userId + "\","
                    + "\"email\":\"" + email + "\","
                    + "\"role\":\"" + role + "\","
                    + "\"tokenType\":\"" + tokenType + "\","
                    + "\"iat\":" + now.getEpochSecond() + ","
                    + "\"exp\":" + expiresAt.getEpochSecond()
                    + "}";

            String encodedHeader = encode(headerJson);
            String encodedPayload = encode(payloadJson);
            String signature = sign(encodedHeader + "." + encodedPayload, secretKey);

            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("JWT 생성 중 오류가 발생했습니다.", exception);
        }
    }

    private Map<String, Object> parsePayload(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                return null;
            }

            byte[] decodedPayloadBytes = Base64.getUrlDecoder().decode(tokenParts[1]);
            String payloadJson = new String(decodedPayloadBytes, StandardCharsets.UTF_8);

            return objectMapper.readValue(payloadJson, new TypeReference<>() {
            });
        } catch (Exception exception) {
            return null;
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        mac.init(secretKeySpec);

        byte[] signatureBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}