package com.codeit.weatherfit.domain.clothes.dto.response;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClothesAttributeDefDto (
        UUID id,
        String name,
        List<String> selectableValues,
        Instant createdAt
) {
    public static ClothesAttributeDefDto from(
            ClothesAttributeType type,
            List<SelectableValue> values
    ) {
        List<String> options = values.stream()
                .map(SelectableValue::getOption)
                .toList();

        return new ClothesAttributeDefDto(
                type.getId(),
                type.getName(),
                options,
                type.getCreatedAt()
        );
    }
}
