package com.codeit.weatherfit.domain.recommendation.dto;

import java.util.List;
import java.util.UUID;

public record RecommendationDto(
        UUID weatherId,
        UUID userId,
        List<RecommendedClothes> clothes
) {
}
