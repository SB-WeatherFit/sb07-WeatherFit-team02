package com.codeit.weatherfit.domain.user.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        @Schema(description = "사용자 ID") UUID id,
        @Schema(description = "가입일시") Instant createdAt,
        @Schema(description = "이메일") String email,
        @Schema(description = "이름") String name,
        @Schema(description = "역할") UserRole role,
        @Schema(description = "잠금 상태") boolean locked
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isLocked()
        );
    }
}