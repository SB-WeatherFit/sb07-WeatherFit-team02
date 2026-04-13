package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.AsWord;
import com.codeit.weatherfit.domain.weather.entity.WindSpeed;
import io.swagger.v3.oas.annotations.media.Schema;

public record WindSpeedResponse(
    @Schema(description = "풍속") double speed,
    @Schema(description = "풍속 단어 표현") AsWord asWord

) {
    public static WindSpeedResponse from(WindSpeed windSpeed) {
        if(windSpeed == null) return null;
        return new WindSpeedResponse(
                windSpeed.getSpeed(),
                windSpeed.getAsWord()
        );
    }
}
