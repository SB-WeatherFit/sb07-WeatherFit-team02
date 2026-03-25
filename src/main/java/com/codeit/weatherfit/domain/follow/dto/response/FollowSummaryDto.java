package com.codeit.weatherfit.domain.follow.dto.response;

import java.util.UUID;

public record FollowSummaryDto(
        UUID followeeId,
        long followerCount,
        long followingCount,
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
