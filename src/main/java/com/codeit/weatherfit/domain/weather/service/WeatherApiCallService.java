package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;

public interface WeatherApiCallService {

    public WeatherApiResponse getWeathersFromNow(LocationResponse locationResponse);
}
