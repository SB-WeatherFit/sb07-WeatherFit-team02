package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;

public interface WeatherService {

    public WeatherResponse getWeather(WeatherRequest weatherRequest);
    public LocationResponse getWeatherLocation(WeatherRequest weatherRequest);
    public WeatherApiResponse getWeatherApiResponse(WeatherRequest weatherRequest);

}
