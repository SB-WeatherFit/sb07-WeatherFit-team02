package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.ClothesSnapshot;

import java.util.List;
import java.util.UUID;

public record Ootd(
        UUID clothesId,
        String name,
        String imageUrl,
        List<Option> attributes
) {
    public static Ootd from(ClothesSnapshot clothesSnapshot, String imageUrl) {
        return new Ootd(
                clothesSnapshot.clothesId(),
                clothesSnapshot.name(),
                imageUrl,
                clothesSnapshot.attributes().stream()
                        .map(Option::new)
                        .toList()
        );
    }

    public record Option(
            String value
    ){}
}
