package com.codeit.weatherfit.domain.feed.dto.response;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedGetResponse(
        @Schema(description = "피드 목록") List<FeedDto> data,
        @Schema(description = "다음 페이지 커서") Instant nextCursor,
        @Schema(description = "다음 페이지 시작 ID") UUID nextIdAfter,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "전체 개수") long totalCount,
        @Schema(description = "정렬 기준") SortBy sortBy,
        @Schema(description = "정렬 방향") SortDirection sortDirection
) {
}
