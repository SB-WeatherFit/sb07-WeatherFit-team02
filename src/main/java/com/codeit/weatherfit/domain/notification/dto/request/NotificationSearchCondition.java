package com.codeit.weatherfit.domain.notification.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record NotificationSearchCondition(
        Instant cursor,
        UUID idAfter,
        @NotNull int limit
) {
}
