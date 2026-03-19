package com.codeit.weatherfit.domain.auth.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAuthTokenStore {

    // 임시 로그인
    private final Map<UUID, String> currentAccessTokenByUserId = new ConcurrentHashMap<>();
    // 임시 로그인
    private final Map<UUID, String> currentRefreshTokenByUserId = new ConcurrentHashMap<>();
    // 임시 로그인
    private final Map<String, UUID> activeRefreshTokenToUserId = new ConcurrentHashMap<>();
    // 임시 로그인
    private final Map<String, Boolean> revokedAccessTokens = new ConcurrentHashMap<>();

    public void register(UUID userId, String accessToken, String refreshToken) {
        String previousAccessToken = currentAccessTokenByUserId.put(userId, accessToken);
        String previousRefreshToken = currentRefreshTokenByUserId.put(userId, refreshToken);

        if (previousAccessToken != null) {
            revokedAccessTokens.put(previousAccessToken, true);
        }

        if (previousRefreshToken != null) {
            activeRefreshTokenToUserId.remove(previousRefreshToken);
        }

        activeRefreshTokenToUserId.put(refreshToken, userId);
    }

    public void revoke(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            revokedAccessTokens.put(accessToken, true);
            currentAccessTokenByUserId.entrySet().removeIf(entry -> entry.getValue().equals(accessToken));
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            activeRefreshTokenToUserId.remove(refreshToken);
            currentRefreshTokenByUserId.entrySet().removeIf(entry -> entry.getValue().equals(refreshToken));
        }
    }

    public UUID findUserIdByRefreshToken(String refreshToken) {
        return activeRefreshTokenToUserId.get(refreshToken);
    }
}