package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ClothesCreateRequest (
        @NotNull
        UUID ownerId,

        @NotBlank
        String name,

        @NotNull
        ClothesType type,

        List<@Valid ClothesAttributeDefCreateRequest> attributes
) {
}
