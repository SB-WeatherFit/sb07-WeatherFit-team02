package com.codeit.weatherfit.domain.follow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record FolloweeSearchCondition(
        @NotNull UUID followerId,
        Instant cursor,
        UUID idAfter,
        @NotNull int limit,
        String nameLike
) {
}
