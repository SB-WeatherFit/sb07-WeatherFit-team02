package com.codeit.weatherfit.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FollowListResponse(
        @Schema(description = "팔로우 목록") List<FollowDto> data,
        @Schema(description = "다음 페이지 커서") Instant nextCursor,
        @Schema(description = "다음 페이지 시작 ID") UUID nextIdAfter,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "전체 개수") long totalCount,
        @Schema(description = "정렬 기준") SortBy sortBy,
        @Schema(description = "정렬 방향") SortDirection sortDirection
) {
    public FollowListResponse(List<FollowDto> data,
                              Instant nextCursor,
                              UUID nextIdAfter,
                              boolean hasNext,
                              long totalCount){
        this(data, nextCursor, nextIdAfter, hasNext, totalCount,
                SortBy.CREATED_AT, SortDirection.DESCENDING);
    }
}
