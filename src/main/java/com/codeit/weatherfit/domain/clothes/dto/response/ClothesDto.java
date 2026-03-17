package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.List;
import java.util.UUID;

public record ClothesDto (
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeDto> attributes
) {
    public static ClothesDto from(Clothes clothes) {
        return new ClothesDto(
                clothes.getId(),
                clothes.getOwner().getId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType(),
                List.of()
        );
    }
}
