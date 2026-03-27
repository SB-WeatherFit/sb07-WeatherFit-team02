package com.codeit.weatherfit.domain.recommendation.dto;

import java.util.List;
import java.util.UUID;

public record AttributesDto(
        UUID definitionId,
        String definitionName,
        List<String> selectableValues,
        String value
) {
}
