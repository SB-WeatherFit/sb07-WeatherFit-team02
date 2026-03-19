package com.codeit.weatherfit.domain.message.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record MessageGetRequest(
        @NotNull
        UUID userId,

        Instant cursor,

        UUID idAfter,

        @NotNull
        @Min(1)
        int limit
) {
}
