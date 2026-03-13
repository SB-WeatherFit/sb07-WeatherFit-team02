package com.codeit.weatherfit.domain.follow.dto.request;

import java.time.Instant;
import java.util.UUID;

public record FolloweeSearchCondition(
        UUID followeeId,
        Instant cursor,
        UUID idAfter,
        int limit,
        String nameLike
) {
}
