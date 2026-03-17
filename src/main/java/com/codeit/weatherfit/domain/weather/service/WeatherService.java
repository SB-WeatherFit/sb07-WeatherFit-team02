package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;

import java.time.Instant;

public interface WeatherService {


    WeatherResponse getWeather(WeatherRequest request, Instant time) throws InterruptedException;
    public LocationResponse getWeatherLocation(WeatherRequest weatherRequest);


}
