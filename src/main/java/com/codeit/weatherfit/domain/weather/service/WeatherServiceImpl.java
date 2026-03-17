package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiCallService weatherApiCallService;

    @Override
    public WeatherResponse getWeather(WeatherRequest request) {
        WeatherApiResponse response = weatherApiCallService.getWeathersFromNow(request);
        Weather weather = weatherFromApiResponse(response);
        return WeatherResponse.from(weather);
    }

    private Weather weatherFromApiResponse(WeatherApiResponse response) {



        return null;
    }
    @Override
    public LocationResponse getWeatherLocation(WeatherRequest request) {
        return null;
    }

    @Override
    public WeatherApiResponse getWeatherApiResponse(WeatherRequest request) {
        return weatherApiCallService.getWeathersFromNow(request);
    }
}
