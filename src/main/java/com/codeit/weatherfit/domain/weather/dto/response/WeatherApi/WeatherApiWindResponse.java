package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

public record WeatherApiWindResponse(

        double speed,
        long deg,
        double gust
) {
}
