package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record FollowDto(
        @Schema(description = "팔로우 ID") UUID id,
        @Schema(description = "팔로이 정보") FollowUser followee,
        @Schema(description = "팔로워 정보") FollowUser follower
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
