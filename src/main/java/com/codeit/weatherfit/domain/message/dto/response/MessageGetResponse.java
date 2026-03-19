package com.codeit.weatherfit.domain.message.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageGetResponse(
        List<DirectMessageDto> data,
        Instant nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        int totalCount,
        SortBy sortBy,
        SortDirection sortDirection
) {
}
