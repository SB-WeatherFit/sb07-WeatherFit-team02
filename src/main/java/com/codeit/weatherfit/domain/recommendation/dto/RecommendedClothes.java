package com.codeit.weatherfit.domain.recommendation.dto;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.UUID;

public record RecommendedClothes(
        UUID clothesId,
        String name,
        String imageUrl,
        ClothesType type,
        AttributesDto attributes
) {
}