package com.codeit.weatherfit.domain.recommendation.controller.docs;

import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "옷차림 추천", description = "날씨 기반 옷차림 추천 API")
public interface RecommendationControllerDocs {

    @Operation(summary = "옷차림 추천 조회", description = "날씨 정보를 기반으로 옷차림을 추천합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(schema = @Schema(implementation = RecommendationDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "날씨 정보 없음")
    })
    ResponseEntity<RecommendationDto> getRecommendations(
            @Parameter(description = "날씨 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID weatherId,
            @Parameter(hidden = true) UUID userId
    );

    @Operation(summary = "LLM 기반 옷차림 추천 조회", description = "LLM을 활용하여 날씨 기반 옷차림을 추천합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(schema = @Schema(implementation = RecommendationDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "날씨 정보 없음")
    })
    ResponseEntity<RecommendationDto> getRecommendationsLLM(
            @Parameter(description = "날씨 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID weatherId,
            @Parameter(hidden = true) UUID userId
    );
}
