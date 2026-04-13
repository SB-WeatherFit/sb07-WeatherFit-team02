package com.codeit.weatherfit.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "알림 검색 조건")
public record NotificationSearchCondition(
        @Schema(description = "페이지네이션 커서 값")
        Instant cursor,

        @Schema(description = "커서 이후 ID")
        UUID idAfter,

        @NotNull
        @Schema(description = "조회 개수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        int limit
) {
}
