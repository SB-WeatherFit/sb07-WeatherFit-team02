package com.codeit.weatherfit.domain.user.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        Instant createdAt,
        String email,
        String name,
        UserRole role,
        boolean locked
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