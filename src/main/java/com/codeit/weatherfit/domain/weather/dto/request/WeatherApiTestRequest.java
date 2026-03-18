package com.codeit.weatherfit.domain.weather.dto.request;

public record WeatherApiTestRequest(
        int numOfRows,
        String baseDate,
        String baseTime,
        double longitude,
        double latitude
) {
}
