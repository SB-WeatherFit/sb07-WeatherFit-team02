package com.codeit.weatherfit.domain.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record RecommendationDto(
        @Schema(description = "날씨 ID") UUID weatherId,
        @Schema(description = "사용자 ID") UUID userId,
        @Schema(description = "추천 의상 목록") List<RecommendedClothes> clothes
) {
}
