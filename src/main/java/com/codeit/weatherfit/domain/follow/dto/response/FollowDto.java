package com.codeit.weatherfit.domain.follow.dto.response;

import java.util.UUID;

public record FollowDto(
        UUID id,
        FollowUser followUser,
        FollowUser follower
) {
}
