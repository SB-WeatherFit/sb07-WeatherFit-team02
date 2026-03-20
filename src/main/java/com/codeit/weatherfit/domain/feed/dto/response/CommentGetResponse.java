package com.codeit.weatherfit.domain.feed.dto.response;

import com.codeit.weatherfit.domain.feed.dto.CommentDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentGetResponse(
        List<CommentDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount
) {
    public SortBy sortBy() {
        return SortBy.CREATED_AT;
    }
    public SortDirection sortDirection() {
        return SortDirection.DESCENDING;
    }

    public enum SortBy {
        CREATED_AT
    }

    public enum SortDirection {
        DESCENDING
    }

}
