package com.codeit.weatherfit.domain.feed.dto.response;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedGetResponse(
        List<FeedDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
}
