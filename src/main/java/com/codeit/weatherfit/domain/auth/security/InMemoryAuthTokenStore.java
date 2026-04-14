package com.codeit.weatherfit.domain.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InMemoryAuthTokenStore {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private static final String ACCESS_TOKEN_BY_USER_ID_KEY = "auth:access:user:%s";
    private static final String REFRESH_TOKEN_BY_USER_ID_KEY = "auth:refresh:user:%s";
    private static final String USER_ID_BY_REFRESH_TOKEN_KEY = "auth:refresh:token:%s";
    private static final String REVOKED_ACCESS_TOKEN_KEY = "auth:access:revoked:%s";

    private final StringRedisTemplate stringRedisTemplate;

    public void register(UUID userId, String accessToken, String refreshToken) {
        String accessTokenByUserIdKey = createAccessTokenByUserIdKey(userId);
        String refreshTokenByUserIdKey = createRefreshTokenByUserIdKey(userId);

        String previousAccessToken = stringRedisTemplate.opsForValue().get(accessTokenByUserIdKey);
        String previousRefreshToken = stringRedisTemplate.opsForValue().get(refreshTokenByUserIdKey);

        if (previousAccessToken != null && !previousAccessToken.isBlank()) {
            stringRedisTemplate.opsForValue().set(
                    createRevokedAccessTokenKey(previousAccessToken),
                    "true",
                    ACCESS_TOKEN_TTL
            );
        }

        if (previousRefreshToken != null && !previousRefreshToken.isBlank()) {
            stringRedisTemplate.delete(createUserIdByRefreshTokenKey(previousRefreshToken));
        }

        stringRedisTemplate.opsForValue().set(accessTokenByUserIdKey, accessToken, ACCESS_TOKEN_TTL);
        stringRedisTemplate.opsForValue().set(refreshTokenByUserIdKey, refreshToken, REFRESH_TOKEN_TTL);
        stringRedisTemplate.opsForValue().set(
                createUserIdByRefreshTokenKey(refreshToken),
                userId.toString(),
                REFRESH_TOKEN_TTL
        );
    }

    public void revoke(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            stringRedisTemplate.opsForValue().set(
                    createRevokedAccessTokenKey(accessToken),
                    "true",
                    ACCESS_TOKEN_TTL
            );
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            String userIdValue = stringRedisTemplate.opsForValue().get(createUserIdByRefreshTokenKey(refreshToken));

            stringRedisTemplate.delete(createUserIdByRefreshTokenKey(refreshToken));

            if (userIdValue != null && !userIdValue.isBlank()) {
                stringRedisTemplate.delete(createRefreshTokenByUserIdKey(UUID.fromString(userIdValue)));
            }
        }
    }

    public void revokeAllByUserId(UUID userId) {
        String accessTokenByUserIdKey = createAccessTokenByUserIdKey(userId);
        String refreshTokenByUserIdKey = createRefreshTokenByUserIdKey(userId);

        String currentAccessToken = stringRedisTemplate.opsForValue().get(accessTokenByUserIdKey);
        String currentRefreshToken = stringRedisTemplate.opsForValue().get(refreshTokenByUserIdKey);

        if (currentAccessToken != null && !currentAccessToken.isBlank()) {
            stringRedisTemplate.opsForValue().set(
                    createRevokedAccessTokenKey(currentAccessToken),
                    "true",
                    ACCESS_TOKEN_TTL
            );
        }

        if (currentRefreshToken != null && !currentRefreshToken.isBlank()) {
            stringRedisTemplate.delete(createUserIdByRefreshTokenKey(currentRefreshToken));
        }

        stringRedisTemplate.delete(accessTokenByUserIdKey);
        stringRedisTemplate.delete(refreshTokenByUserIdKey);
    }

    public UUID findUserIdByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }

        String userIdValue = stringRedisTemplate.opsForValue().get(createUserIdByRefreshTokenKey(refreshToken));
        if (userIdValue == null || userIdValue.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(userIdValue);
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean isRevokedAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return false;
        }

        Boolean hasKey = stringRedisTemplate.hasKey(createRevokedAccessTokenKey(accessToken));
        return Boolean.TRUE.equals(hasKey);
    }

    private String createAccessTokenByUserIdKey(UUID userId) {
        return ACCESS_TOKEN_BY_USER_ID_KEY.formatted(userId);
    }

    private String createRefreshTokenByUserIdKey(UUID userId) {
        return REFRESH_TOKEN_BY_USER_ID_KEY.formatted(userId);
    }

    private String createUserIdByRefreshTokenKey(String refreshToken) {
        return USER_ID_BY_REFRESH_TOKEN_KEY.formatted(refreshToken);
    }

    private String createRevokedAccessTokenKey(String accessToken) {
        return REVOKED_ACCESS_TOKEN_KEY.formatted(accessToken);
    }
}