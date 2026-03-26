package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;

import java.util.UUID;

public record PromptClothesInfo(
        UUID clothesId,
        String type,
        String clothesName
) {
    public static PromptClothesInfo from(Clothes clothes) {
        return new PromptClothesInfo(clothes.getId(),
                clothes.getType().name(),
                clothes.getName());
    }
}
