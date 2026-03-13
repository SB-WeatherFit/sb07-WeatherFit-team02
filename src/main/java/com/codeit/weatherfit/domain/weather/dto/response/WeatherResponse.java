package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.entity.*;

import java.time.Instant;
import java.util.UUID;

public record WeatherResponse(
        UUID id,
        Instant forecastedAt,
        Instant forecastAt,
        LocationResponse location,
        SkyStatus skyStatus,
        PrecipitaionResponse precipitation,
        HumidityResponse humidity,
        TemperatureResponse temperature,
        WindSpeedResponse windSpeed
) {
    public static WeatherResponse from(Weather weather) {

        return new WeatherResponse(
                weather.getId(),
                weather.getForecastedAt(),
                weather.getForecastAt(),
                LocationResponse.from(weather.getLocation()),
                weather.getSkyStatus(),
                PrecipitaionResponse.from(weather.getPrecipitation()),
                HumidityResponse.from(weather.getHumidity()),
                TemperatureResponse.from(weather.getTemperature()),
                WindSpeedResponse.from(weather.getWindSpeed())
        );
    }
}
