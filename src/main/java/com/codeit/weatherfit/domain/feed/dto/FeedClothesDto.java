package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.FeedClothes;

public record FeedClothesDto(
        String name,
        String imageUrl
) {
    public static FeedClothesDto from(FeedClothes feedClothes) {
        return new FeedClothesDto(feedClothes.getName(), feedClothes.getImageUrl());
    }
}
