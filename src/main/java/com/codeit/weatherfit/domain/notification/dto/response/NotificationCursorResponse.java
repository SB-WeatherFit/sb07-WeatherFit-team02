package com.codeit.weatherfit.domain.notification.dto.response;

import com.codeit.weatherfit.domain.follow.dto.response.SortBy;
import com.codeit.weatherfit.domain.follow.dto.response.SortDirection;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NotificationCursorResponse(
        List<NotificationDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
    public NotificationCursorResponse(List<NotificationDto> data,
                                      Instant nextCursor,
                                      UUID nextIdAfter,
                                      boolean hasNext,
                                      long totalCount) {
        this(data, nextCursor, nextIdAfter, hasNext, totalCount,
                SortBy.CREATED_AT, SortDirection.DESCENDING);
    }
}
