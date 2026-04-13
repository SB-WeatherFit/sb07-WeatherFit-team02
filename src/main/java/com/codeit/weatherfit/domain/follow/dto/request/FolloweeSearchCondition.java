package com.codeit.weatherfit.domain.follow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "팔로잉 검색 조건")
public record FolloweeSearchCondition(
        @NotNull
        @Schema(description = "팔로워 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID followerId,

        @Schema(description = "페이지네이션 커서 값")
        Instant cursor,

        @Schema(description = "커서 이후 ID")
        UUID idAfter,

        @NotNull
        @Schema(description = "조회 개수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        int limit,

        @Schema(description = "이름 검색")
        String nameLike
) {
}
