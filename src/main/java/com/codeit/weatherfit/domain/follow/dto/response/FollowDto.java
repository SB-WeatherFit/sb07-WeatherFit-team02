package com.codeit.weatherfit.domain.follow.dto.response;

import com.codeit.weatherfit.domain.user.dto.response.UserSummary;

import java.util.UUID;

public record FollowDto(
        UUID id,
        UserSummary followee,
        UserSummary follower
) {
    public static FollowDto create(UUID followId, UserSummary followee, UserSummary follower) {
        return new FollowDto(
                followId,
                followee,
                follower
        );
    }
}
