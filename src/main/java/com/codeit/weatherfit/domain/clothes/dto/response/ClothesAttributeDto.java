package com.codeit.weatherfit.domain.clothes.dto.response;

import java.util.UUID;

public record ClothesAttributeDto (
        UUID definitionId,
        String option
) {
}