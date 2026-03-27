package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.user.entity.User;

import java.util.UUID;

public record FollowUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
    public static FollowUser from(User user, String profileImageUrl) {
        return new FollowUser(
                user.getId(),
                user.getName(),
                profileImageUrl
        );
    }
}
