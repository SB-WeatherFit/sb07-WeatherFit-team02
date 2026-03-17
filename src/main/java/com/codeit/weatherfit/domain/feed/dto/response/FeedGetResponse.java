package com.codeit.weatherfit.domain.feed.dto.response;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;

import java.util.List;

public record FeedGetResponse(
        List<FeedDto> data,
        String nextCursor,
        String nextIdAfter,
        boolean hasNext,
        Long totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
}
