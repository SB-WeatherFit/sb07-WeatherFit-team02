package com.codeit.weatherfit.domain.auth.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAuthTokenStore {

    private final Map<UUID, String> currentAccessTokenByUserId = new ConcurrentHashMap<>();
    private final Map<UUID, String> currentRefreshTokenByUserId = new ConcurrentHashMap<>();
    private final Map<String, UUID> activeRefreshTokenToUserId = new ConcurrentHashMap<>();
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

    public void revokeAllByUserId(UUID userId) {
        String currentAccessToken = currentAccessTokenByUserId.remove(userId);
        if (currentAccessToken != null && !currentAccessToken.isBlank()) {
            revokedAccessTokens.put(currentAccessToken, true);
        }

        String currentRefreshToken = currentRefreshTokenByUserId.remove(userId);
        if (currentRefreshToken != null && !currentRefreshToken.isBlank()) {
            activeRefreshTokenToUserId.remove(currentRefreshToken);
        }
    }

    public UUID findUserIdByRefreshToken(String refreshToken) {
        return activeRefreshTokenToUserId.get(refreshToken);
    }

    public boolean isRevokedAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return false;
        }

        return revokedAccessTokens.containsKey(accessToken);
    }
}