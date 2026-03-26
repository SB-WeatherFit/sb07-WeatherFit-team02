package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
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
    public static ClothesDto from(Clothes clothes,
                                  List<ClothesAttribute> attributes) {

        List<ClothesAttributeDto> attributesDtos = attributes.stream()
                .map(attr -> new ClothesAttributeDto(
                        attr.getOption()
                                .getClothesAttributeType()
                                .getId(),
                        attr.getOption()
                                .getOption()
                )).toList();

        return new ClothesDto(
                clothes.getId(),
                clothes.getOwner().getId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType(),
                attributesDtos
        );
    }
}
