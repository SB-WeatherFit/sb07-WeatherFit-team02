package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtTokenProvider {

    // 임시 로그인
    private static final String SECRET_KEY = "temporary-weatherfit-secret-key-for-auth";
    // 임시 로그인
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 60L * 60L;

    public String generateAccessToken(User user) {
        try {
            Instant now = Instant.now();
            Instant expiresAt = now.plusSeconds(ACCESS_TOKEN_EXPIRE_SECONDS);

            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = "{"
                    + "\"sub\":\"" + user.getId() + "\","
                    + "\"email\":\"" + user.getEmail() + "\","
                    + "\"role\":\"" + user.getRole().name() + "\","
                    + "\"iat\":" + now.getEpochSecond() + ","
                    + "\"exp\":" + expiresAt.getEpochSecond()
                    + "}";

            String encodedHeader = encode(headerJson);
            String encodedPayload = encode(payloadJson);
            String signature = sign(encodedHeader + "." + encodedPayload);

            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("JWT 생성 중 오류가 발생했습니다.", exception);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        mac.init(secretKeySpec);

        byte[] signatureBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}