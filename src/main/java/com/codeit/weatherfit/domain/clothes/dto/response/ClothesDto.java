package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.List;
import java.util.UUID;

public record ClothesDto (
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeDto> attributes
) {
}
