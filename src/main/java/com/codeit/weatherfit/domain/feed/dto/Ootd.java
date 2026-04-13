package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.ClothesSnapshot;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record Ootd(
        @Schema(description = "의상 ID") UUID clothesId,
        @Schema(description = "의상 이름") String name,
        @Schema(description = "의상 이미지 URL") String imageUrl,
        @Schema(description = "속성 목록") List<Option> attributes
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
            @Schema(description = "속성 값") String value
    ){}
}
