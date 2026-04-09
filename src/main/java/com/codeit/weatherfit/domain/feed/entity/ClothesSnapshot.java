package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;

import java.util.List;
import java.util.UUID;

public record ClothesSnapshot(
        UUID clothesId,
        String name,
        String imageKey,
        List<String> attributes
) {
    public static ClothesSnapshot from(Clothes clothes, List<String> attributes) {
        return new ClothesSnapshot(
                clothes.getId(),
                clothes.getName(),
                clothes.getImageKey(),
                attributes
        );
    }
}
