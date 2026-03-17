package com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi;

public record WeatherAdministrationTime(
        String category,
        String fcstValue,
        String baseDate,
        String baseTime,
        String fcstDate,
        String fcstTime
) {
}
