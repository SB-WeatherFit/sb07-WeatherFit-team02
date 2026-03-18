package com.codeit.weatherfit.domain.auth.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAuthTokenStore {

    // 임시 로그인
    private final Map<UUID, String> currentTokenByUserId = new ConcurrentHashMap<>();
    // 임시 로그인
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    public void register(UUID userId, String accessToken) {
        String previousToken = currentTokenByUserId.put(userId, accessToken);

        if (previousToken != null) {
            revokedTokens.add(previousToken);
        }
    }

    public void revoke(String accessToken) {
        revokedTokens.add(accessToken);
        currentTokenByUserId.entrySet().removeIf(entry -> entry.getValue().equals(accessToken));
    }

    public boolean isRevoked(String accessToken) {
        return revokedTokens.contains(accessToken);
    }
}