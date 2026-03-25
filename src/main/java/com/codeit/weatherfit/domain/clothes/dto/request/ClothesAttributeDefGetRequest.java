package com.codeit.weatherfit.domain.clothes.dto.request;

import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import jakarta.validation.constraints.NotNull;

public record ClothesAttributeDefGetRequest(
        @NotNull
        SortBy sortBy,
        @NotNull
        SortDirection sortDirection,
        String keyword
) {
}
