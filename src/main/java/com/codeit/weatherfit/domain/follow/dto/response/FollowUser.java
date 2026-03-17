package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.util.UUID;

public record FollowUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
    public static FollowUser from(Profile profile) {
        return new FollowUser(
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getProfileImageUrl()
        );
    }
}
