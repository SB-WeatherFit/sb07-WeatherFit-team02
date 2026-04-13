package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MessageUser(
        @Schema(description = "사용자 ID") UUID userId,
        @Schema(description = "이름") String name,
        @Schema(description = "프로필 이미지 URL") String profileImageUrl
) {
    public static MessageUser from(User user, String profileImageUrl) {
        return new MessageUser(
                user.getId(),
                user.getName(),
                profileImageUrl
        );
    }
}