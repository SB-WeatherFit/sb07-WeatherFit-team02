package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.util.List;
import java.util.UUID;

public record ClothesDto(
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeDto> attributes
) {
    public static ClothesDto from(Clothes clothes,
                                  List<ClothesAttribute> attributes,
                                  String imageUrl) {

        List<ClothesAttributeDto> attributesDtos = attributes.stream()
                .map(ClothesAttributeDto::from)
                .toList();

        return new ClothesDto(
                clothes.getId(),
                clothes.getOwner().getId(),
                clothes.getName(),
                imageUrl,
                clothes.getType(),
                attributesDtos
        );
    }
}
