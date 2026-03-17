package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;

import java.time.Instant;
import java.util.List;

public interface WeatherApiCallService {

//    public WeatherResponse getWeatherFromAdministration(WeatherRequest request, Instant time);
    public List<WeatherResponse> getWeatherLisFromAdministration(WeatherRequest request, Instant time);
}
