package com.codeit.weatherfit.domain.weather.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@Schema(description = "날씨 조회 요청")
public record WeatherRequest(
        @Schema(description = "경도", example = "126.9780")
        double longitude,

        @Schema(description = "위도", example = "37.5665")
        double latitude
) {
}
