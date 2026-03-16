package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.List;

public record ClothesUpdateRequest (
        String name,
        ClothesType type,
        List<ClothesAttributeDefUpdateRequest> attributes
) {
}
