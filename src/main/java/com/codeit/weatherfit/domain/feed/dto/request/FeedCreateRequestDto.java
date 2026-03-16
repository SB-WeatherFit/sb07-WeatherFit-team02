package com.codeit.weatherfit.domain.feed.dto.request;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;

import java.util.List;
import java.util.UUID;

public record FeedCreateRequestDto(
        UUID userId,
        UUID weatherId,
        List<Clothes> clothedIds,
        String content
) {
}
