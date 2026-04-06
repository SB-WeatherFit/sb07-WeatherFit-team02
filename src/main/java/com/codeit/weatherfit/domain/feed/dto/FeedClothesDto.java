package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;

import java.util.List;
import java.util.UUID;

public record FeedClothesDto(
        UUID clothesId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeWithDefDto> attributes
) {
    public static FeedClothesDto from(FeedClothes feedClothes,
                                      String imageUrl,
                                      List<ClothesAttributeWithDefDto> attributes) {
        Clothes clothes = feedClothes.getClothes();
        return new FeedClothesDto(
                clothes.getId(),
                clothes.getName(),
                imageUrl,
                clothes.getType(),
                attributes
        );
    }
}
