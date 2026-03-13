package com.codeit.weatherfit.domain.follow.dto.response;

import java.util.UUID;

public record FollowSummaryDto(
        UUID followeeId,
        long followerCount,
        long followeeCount,
        boolean followedByMe,
        UUID followedByMeId,
        boolean followingMe
) {
}
