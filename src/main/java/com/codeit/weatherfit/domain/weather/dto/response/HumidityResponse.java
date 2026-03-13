package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Humidity;

public record HumidityResponse(
        double current,
        double comparedTodayBefore
) {
    public static HumidityResponse from(Humidity humidity) {
        if(humidity == null) return null;
        return new HumidityResponse(
                humidity.getCurrent(),
                humidity.getComparedToDayBefore()
        );
    }
}
