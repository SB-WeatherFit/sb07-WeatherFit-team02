package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Humidity;
import io.swagger.v3.oas.annotations.media.Schema;

public record HumidityResponse(
        @Schema(description = "현재 습도") double current,
        @Schema(description = "전일 대비 습도 변화") double comparedTodayBefore
) {
    public static HumidityResponse from(Humidity humidity) {
        if(humidity == null) return null;
        return new HumidityResponse(
                humidity.getCurrent(),
                humidity.getComparedToDayBefore()
        );
    }
}
