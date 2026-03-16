package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Temperature;

public record TemperatureResponse(
        double current,
        double comparedToDayBefore,
        double min,
        double max
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
