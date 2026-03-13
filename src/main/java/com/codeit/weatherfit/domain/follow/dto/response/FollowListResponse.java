package com.codeit.weatherfit.domain.follow.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FollowListResponse(
        List<FollowDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
}
