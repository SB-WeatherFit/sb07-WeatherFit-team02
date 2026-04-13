package com.codeit.weatherfit.domain.feed.dto.request;

import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "피드 조회 요청")
public record FeedGetRequest(

        @Schema(description = "페이지네이션 커서 값")
        Instant cursor,

        @Schema(description = "커서 이후 ID")
        UUID idAfter,

        @NotNull
        @Schema(description = "조회 개수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        int limit,

        @NotNull
        @Schema(description = "정렬 기준 (createdAt, likeCount)", example = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
        SortBy sortBy,

        @NotNull
        @Schema(description = "정렬 방향 (ASCENDING, DESCENDING)", example = "DESCENDING", requiredMode = Schema.RequiredMode.REQUIRED)
        SortDirection sortDirection,

        @Schema(description = "키워드 검색")
        String keywordLike,

        @Schema(description = "하늘 상태 필터")
        SkyStatus skyStatusEqual,

        @Schema(description = "강수 유형 필터")
        PrecipitationType precipitationTypeEqual,

        @Schema(description = "작성자 ID 필터")
        UUID authorIdEqual
) {
}
