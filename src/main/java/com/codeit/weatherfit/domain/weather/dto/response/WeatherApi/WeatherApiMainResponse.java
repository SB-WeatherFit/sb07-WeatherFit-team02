package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

public record WeatherApiMainResponse(
        double temp,
        double feels_like,
        double temp_min,
        double temp_max,
        long pressure,
        long sea_level,
        long grnd_level,
        long humidity,
        double temp_kf
) {
}
