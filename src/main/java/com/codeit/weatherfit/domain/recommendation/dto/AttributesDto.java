package com.codeit.weatherfit.domain.recommendation.dto;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record AttributesDto(
        @Schema(description = "속성 정의 ID") UUID definitionId,
        @Schema(description = "속성 정의 이름") String definitionName,
        @Schema(description = "선택 가능한 값 목록") List<String> selectableValues,
        @Schema(description = "선택된 값") String value
) {
    public static AttributesDto from(ClothesAttribute clothesAttribute, List<String> selectableValues) {
        return new AttributesDto(
                clothesAttribute.getOption().getClothesAttributeType().getId(),
                clothesAttribute.getOption().getClothesAttributeType().getName(),
                selectableValues,
                clothesAttribute.getOption().getOption()
        );
    }
}
