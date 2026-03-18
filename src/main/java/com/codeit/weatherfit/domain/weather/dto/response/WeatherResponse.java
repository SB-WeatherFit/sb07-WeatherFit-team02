package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.entity.*;

import java.time.Instant;
import java.util.List;
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

        LocationResponse locationResponse = new LocationResponse(
                weather.getLatitude(),
                weather.getLongitude(),
                (int)weather.getLongitude(),
                (int) weather.getLatitude(),
                List.of(weather.getAddressFirst(), weather.getAddressSecond(), weather.getAddressThird())
        );

        PrecipitaionResponse precipitaionResponse = new PrecipitaionResponse(
                weather.getType(),
                weather.getAmount(),
                weather.getProbability()
        );

        HumidityResponse humidityResponse = new HumidityResponse(
                weather.getCurrent(),
                weather.getComparedToDayBefore()
        );

        TemperatureResponse temperatureResponse = new TemperatureResponse(
                weather.getTemperatureCurrent(),
                weather.getTemperatureComparedToDayBefore(),
                weather.getMin(),
                weather.getMax()
        );

        WindSpeedResponse windSpeedResponse = new WindSpeedResponse(
                weather.getSpeed(),
                weather.getAsWord()
        );
        return new WeatherResponse(
                weather.getId(),
                weather.getForecastedAt(),
                weather.getForecastAt(),
                locationResponse,
                weather.getSkyStatus(),
                precipitaionResponse,
                humidityResponse,
                temperatureResponse,
                windSpeedResponse
        );
    }
}
