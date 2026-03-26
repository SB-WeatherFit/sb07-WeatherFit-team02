package com.codeit.weatherfit.domain.recommendation.ai;

import java.util.List;
import java.util.UUID;

public record ClothesSetResponse(
    List<UUID> items
) {}