package com.codeit.weatherfit.domain.recommendation.dto;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record RecommendedClothes(
        @Schema(description = "의상 ID") UUID clothesId,
        @Schema(description = "의상 이름") String name,
        @Schema(description = "의상 이미지 URL") String imageUrl,
        @Schema(description = "의상 종류") ClothesType type,
        @Schema(description = "속성 목록") List<AttributesDto> attributes
) {
}