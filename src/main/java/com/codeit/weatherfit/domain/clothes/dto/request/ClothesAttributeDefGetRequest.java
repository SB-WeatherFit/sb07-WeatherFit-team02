package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "속성 정의 조회 요청")
public record ClothesAttributeDefGetRequest(
        @NotNull
        @Schema(description = "정렬 기준", requiredMode = Schema.RequiredMode.REQUIRED)
        SortBy sortBy,
        @NotNull
        @Schema(description = "정렬 방향", requiredMode = Schema.RequiredMode.REQUIRED)
        SortDirection sortDirection,
        @Schema(description = "키워드 검색")
        String keyword
) {
}
