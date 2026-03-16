package com.codeit.weatherfit.domain.feed.dto.request;

import java.util.List;
import java.util.UUID;

public record FeedCreateRequest(
        UUID userId,
        UUID weatherId,
        List<UUID> clothesIds,
        String content
) {
}
