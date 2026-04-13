package com.codeit.weatherfit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record UserDtoCursorResponse(
        @Schema(description = "사용자 목록") List<UserDto> data,
        @Schema(description = "다음 페이지 커서") String nextCursor,
        @Schema(description = "다음 페이지 시작 ID") UUID nextIdAfter,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "전체 개수") long totalCount,
        @Schema(description = "정렬 기준") String sortBy,
        @Schema(description = "정렬 방향") String sortDirection
) {
    public static UserDtoCursorResponse of(
            List<UserDto> data,
            String nextCursor,
            UUID nextIdAfter,
            boolean hasNext,
            long totalCount,
            String sortBy,
            String sortDirection
    ) {
        return new UserDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                sortBy,
                sortDirection
        );
    }
}