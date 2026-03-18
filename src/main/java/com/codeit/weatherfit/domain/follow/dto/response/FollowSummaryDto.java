package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.follow.entity.Follow;

import java.util.UUID;

public record FollowSummaryDto(
        UUID followeeId,
        long followerCount,
        long followeeCount,
        boolean followedByMe,
        UUID followedByMeId,
        boolean followingMe
) {
    public static  FollowSummaryDto create(UUID followeeId,
                                         long followerCount,
                                         long followeeCount,
                                         boolean followedByMe,
                                         UUID followedByMeId,
                                         boolean followingMe ) {
        return new FollowSummaryDto(
                followeeId,
                followerCount,
                followeeCount,
                followedByMe,
                followedByMeId,
                followingMe
        );
    }
}
