package com.codeit.weatherfit.domain.message.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageCursorResponse(
        List<MessageDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
    public MessageCursorResponse(List<MessageDto> data,
                                 Instant nextCursor,
                                 UUID nextIdAfter,
                                 boolean hasNext,
                                 long totalCount) {
        this(data, nextCursor, nextIdAfter, hasNext, totalCount,
                SortBy.CREATED_AT, SortDirection.DESCENDING);
    }
}
