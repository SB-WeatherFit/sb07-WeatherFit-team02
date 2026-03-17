package com.codeit.weatherfit.domain.user.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.user.entity.User;

import java.util.UUID;

public record UserSummary(
        UUID userId,
        String name,
        String profileImageUrl
) {
    public static UserSummary from(User user, Profile profile) {
        return new UserSummary(
                user.getId(),
                user.getName(),
                profile == null ? null : profile.getProfileImageUrl()
        );
    }
}