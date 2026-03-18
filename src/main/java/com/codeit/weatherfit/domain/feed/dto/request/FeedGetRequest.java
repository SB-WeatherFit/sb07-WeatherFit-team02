package com.codeit.weatherfit.domain.feed.dto.request;

import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record FeedGetRequest(

        Instant cursor,

        UUID idAfter,

        @NotNull
        int limit,

        @NotNull
        SortBy sortBy,

        @NotNull
        SortDirection sortDirection,

        String keywordLike,

        SkyStatus skyStatusEqual,

        PrecipitationType precipitationTypeEqual,

        UUID authorIdEqual
) {
}
