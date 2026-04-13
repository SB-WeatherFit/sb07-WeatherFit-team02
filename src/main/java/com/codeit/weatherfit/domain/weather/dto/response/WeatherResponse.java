package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record WeatherResponse(
        @Schema(description = "날씨 ID") UUID id,
        @Schema(description = "예보 생성 시각") Instant forecastedAt,
        @Schema(description = "예보 대상 시각") Instant forecastAt,
        @Schema(description = "위치 정보") LocationResponse location,
        @Schema(description = "하늘 상태") SkyStatus skyStatus,
        @Schema(description = "강수 정보") PrecipitaionResponse precipitation,
        @Schema(description = "습도 정보") HumidityResponse humidity,
        @Schema(description = "기온 정보") TemperatureResponse temperature,
        @Schema(description = "풍속 정보") WindSpeedResponse windSpeed
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
