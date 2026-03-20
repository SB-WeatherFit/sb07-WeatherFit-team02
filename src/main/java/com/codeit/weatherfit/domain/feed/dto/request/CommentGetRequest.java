package com.codeit.weatherfit.domain.feed.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CommentGetRequest(
        @NotNull(message = "feedId는 필수입니다.")
        UUID feedId,

        Instant cursor,
        UUID idAfter,

        @NotNull(message = "limit은 필수입니다.")
        @Min(value = 1, message = "최소 1개 이상 조회해야합니다.")
        int limit
) {
}
