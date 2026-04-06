package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;

import java.util.UUID;

public record FollowDto(
        UUID id,
        FollowUser followee,
        FollowUser follower
) {
    public static FollowDto create(
            UUID followId,
            User followeeUser,
            String followeeProfileImageUrl,
            User followerUser,
            String followerProfileImageUrl
    ) {

        FollowUser followee = FollowUser.from(followeeUser,  followeeProfileImageUrl);
        FollowUser follower = FollowUser.from(followerUser,   followerProfileImageUrl);
        return new FollowDto(
                followId,
                followee,
                follower
        );
    }
}
