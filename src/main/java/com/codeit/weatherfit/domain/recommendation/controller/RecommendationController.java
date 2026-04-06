package com.codeit.weatherfit.domain.recommendation.controller;

import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;
import com.codeit.weatherfit.domain.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationDto> getRecommendations(
            @RequestParam UUID weatherId,
            @AuthenticationPrincipal(expression = "userId") UUID userId){
        RecommendationDto result = recommendationService.getRecommendations(weatherId, userId);
        return ResponseEntity.ok().body(result);
    }
}
