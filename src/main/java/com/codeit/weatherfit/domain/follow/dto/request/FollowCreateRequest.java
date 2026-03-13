package com.codeit.weatherfit.domain.follow.dto.request;

import java.util.UUID;

public record FollowCreateRequest(
        UUID followeeId,
        UUID followerId
) {
}
