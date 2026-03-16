package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.List;
import java.util.UUID;

public record ClothesCreateRequest (
        UUID ownerId,
        String name,
        ClothesType type,
        List<ClothesAttributeDefCreateRequest> attributes
) {
}
