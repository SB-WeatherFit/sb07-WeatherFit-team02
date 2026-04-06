package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;

import java.util.UUID;

public record MessageUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
    public static MessageUser from(User user, String profileImageUrl) {
        return new MessageUser(
                user.getId(),
                user.getName(),
                profileImageUrl
        );
    }
}