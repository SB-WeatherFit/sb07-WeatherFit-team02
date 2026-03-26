package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.util.UUID;

public record MessageUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
    public static MessageUser from(Profile profile) {
        return new MessageUser(
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getProfileImageKey()
        );
    }
}