package com.codeit.weatherfit.domain.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "메시지 조회 요청")
public record MessageGetRequest(
        @NotNull
        @Schema(description = "상대방 사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,

        @Schema(description = "페이지네이션 커서 값")
        Instant cursor,

        @Schema(description = "커서 이후 ID")
        UUID idAfter,

        @NotNull
        @Min(1)
        @Schema(description = "조회 개수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer limit
) {
}
