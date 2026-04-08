package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.codeit.weatherfit.domain.weather.entity.Weather;

public record WeatherSnapshot(
        SkyStatus skyStatus,
        PrecipitationType type,
        TemperatureSnapshot temperature

) {
    public static WeatherSnapshot from(Weather weather) {
        return new WeatherSnapshot(
                weather.getSkyStatus(),
                weather.getType(),
                new TemperatureSnapshot(weather.getTemperatureCurrent(),
                        weather.getMin(),
                        weather.getMax())
        );
    }

    public record TemperatureSnapshot(
            double current,
            double min,
            double max
    ) {
    }
}
