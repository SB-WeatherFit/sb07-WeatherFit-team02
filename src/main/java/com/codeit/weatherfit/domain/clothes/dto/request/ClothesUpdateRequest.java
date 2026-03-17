package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ClothesUpdateRequest (
        @NotBlank
        String name,

        @NotNull
        ClothesType type,

        List<@Valid ClothesAttributeDefUpdateRequest> attributes
) {
}
