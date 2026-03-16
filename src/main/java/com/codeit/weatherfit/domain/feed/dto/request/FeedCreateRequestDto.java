package com.codeit.weatherfit.domain.feed.dto.request;

import java.util.List;
import java.util.UUID;

public record FeedCreateRequestDto(
        UUID userId,
        UUID weatherId,
        List<UUID> clothesIds,
        String content
) {
}
