package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;


public interface WeatherRepositoryCustom {

    List<Weather> getWeatherByLocation(double longitude, double latitude, Instant forecastedAt);
    Weather getSingleWeatherByLocation(double longitude, double latitude);
}
