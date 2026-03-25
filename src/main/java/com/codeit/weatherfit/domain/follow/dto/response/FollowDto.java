package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.util.UUID;

public record FollowDto(
        UUID id,
        FollowUser followee,
        FollowUser follower
) {
    public static FollowDto create(UUID followId, Profile followeeProfile, Profile followerProfile) {
        FollowUser followee = FollowUser.from(followeeProfile);
        FollowUser follower = FollowUser.from(followerProfile);
        return new FollowDto(
                followId,
                followee,
                follower
        );
    }
}
