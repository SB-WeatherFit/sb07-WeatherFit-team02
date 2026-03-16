package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.AsWord;
import com.codeit.weatherfit.domain.weather.entity.WindSpeed;

public record WindSpeedResponse(
    double speed,
    AsWord asWord

) {
    public static WindSpeedResponse from(WindSpeed windSpeed) {
        if(windSpeed == null) return null;
        return new WindSpeedResponse(
                windSpeed.getSpeed(),
                windSpeed.getAsWord()
        );
    }
}
