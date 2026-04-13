package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Temperature;
import io.swagger.v3.oas.annotations.media.Schema;

public record TemperatureResponse(
        @Schema(description = "현재 기온") double current,
        @Schema(description = "전일 대비 기온 변화") double comparedToDayBefore,
        @Schema(description = "최저 기온") double min,
        @Schema(description = "최고 기온") double max
) {
    public static TemperatureResponse from(Temperature temperature){
        if(temperature == null) return null;
        return new TemperatureResponse(
                temperature.getCurrent(),
                temperature.getComparedToDayBefore(),
                temperature.getMin(),
                temperature.getMax()
        );
    }
}
