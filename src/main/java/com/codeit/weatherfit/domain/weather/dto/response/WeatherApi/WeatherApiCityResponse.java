package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

public record WeatherApiCityResponse(
        long id,
        String name,
        WeatherApiCoordResponse coord,
        String country,
        long population,
        long timezone,
        long sunrise,
        long sunset

) {
}
