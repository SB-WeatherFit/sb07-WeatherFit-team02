package com.codeit.weatherfit.domain.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record AttributesDto(
        @Schema(description = "속성 정의 ID") UUID definitionId,
        @Schema(description = "속성 정의 이름") String definitionName,
        @Schema(description = "선택 가능한 값 목록") List<String> selectableValues,
        @Schema(description = "선택된 값") String value
) {
}
