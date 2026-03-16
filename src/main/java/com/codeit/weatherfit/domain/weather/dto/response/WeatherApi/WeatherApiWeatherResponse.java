package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

public record WeatherApiWeatherResponse(

        long id,
        String main,
        String description,
        String icon
) {
}
