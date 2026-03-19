package com.codeit.weatherfit.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;

public record ClothesDtoCursorResponse(
        List<ClothesDto> data,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        int totalCount,
        String sortBy,
        String sortDirection
) {
}
