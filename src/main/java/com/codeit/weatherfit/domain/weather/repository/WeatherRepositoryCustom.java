package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;


public interface WeatherRepositoryCustom {

    List<WeatherResponse> getWeatherByLocation(WeatherRequest request, Instant time);
    boolean isDataExist(WeatherRequest request, Instant time);
}
