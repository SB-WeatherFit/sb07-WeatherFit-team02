package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;

import java.util.UUID;

public interface RecommendationService {
    RecommendationDto getRecommendations(UUID weatherId, UUID userId);
    RecommendationDto getRecommendationsLLM(UUID weatherId, UUID userId);
}
