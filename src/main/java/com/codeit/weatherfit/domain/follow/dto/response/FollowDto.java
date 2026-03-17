package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.util.UUID;

public record FollowDto(
        UUID id,
        FollowUser followUser,
        FollowUser follower
) {
    public static FollowDto create(UUID followId, Profile followeeProfile, Profile followerProfile) {
        FollowUser followUser = FollowUser.from(followeeProfile);
        FollowUser follower = FollowUser.from(followerProfile);
        return new FollowDto(
                followId,
                followUser,
                follower
        );
    }
}
