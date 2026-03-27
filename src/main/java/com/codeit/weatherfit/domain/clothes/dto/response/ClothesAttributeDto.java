package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;

import java.util.UUID;

public record ClothesAttributeDto (
        UUID definitionId,
        String value
) {
    public static ClothesAttributeDto from(ClothesAttribute attribute) {
        return new ClothesAttributeDto(
                attribute.getOption().getClothesAttributeType().getId(),
                attribute.getOption().getOption()
        );
    }
}