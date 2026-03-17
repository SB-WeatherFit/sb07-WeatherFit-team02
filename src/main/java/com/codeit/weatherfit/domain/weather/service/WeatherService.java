package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WeatherService {


    public List<WeatherResponse> create(WeatherRequest request, Instant time);
    public void delete(UUID id);
    public WeatherResponse getWeather(UUID id);


}
