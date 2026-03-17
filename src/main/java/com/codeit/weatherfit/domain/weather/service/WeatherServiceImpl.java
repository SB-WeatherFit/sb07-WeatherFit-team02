package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiCallService weatherApiCallService;

    @Override
    public WeatherResponse getWeather(WeatherRequest request, Instant time) throws InterruptedException {
        return weatherApiCallService.getWeatherFromAdministration(request,time);


    }

    @Override
    public LocationResponse getWeatherLocation(WeatherRequest request) {
        return null;
    }

}
