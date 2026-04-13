package com.codeit.weatherfit.domain.feed.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "댓글 조회 요청")
public record CommentGetRequest(
        @NotNull(message = "feedId는 필수입니다.")
        @Schema(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID feedId,

        @Schema(description = "페이지네이션 커서 값")
        Instant cursor,

        @Schema(description = "커서 이후 ID")
        UUID idAfter,

        @NotNull(message = "limit은 필수입니다.")
        @Min(value = 1, message = "최소 1개 이상 조회해야합니다.")
        @Schema(description = "조회 개수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        int limit
) {
}
