package com.codeit.weatherfit.domain.feed.dto.response;

import com.codeit.weatherfit.domain.feed.dto.CommentDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentGetResponse(
        @Schema(description = "댓글 목록") List<CommentDto> data,
        @Schema(description = "다음 페이지 커서") Instant nextCursor,
        @Schema(description = "다음 페이지 시작 ID") UUID nextIdAfter,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "전체 개수") long totalCount
) {
    public SortBy sortBy() {
        return SortBy.CREATED_AT;
    }
    public SortDirection sortDirection() {
        return SortDirection.DESCENDING;
    }

    public enum SortBy {
        CREATED_AT
    }

    public enum SortDirection {
        DESCENDING
    }

}
