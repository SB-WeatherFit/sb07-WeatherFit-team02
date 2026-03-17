package com.codeit.weatherfit.domain.weather.exception;

import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherCategoryType;
import com.codeit.weatherfit.global.exception.ErrorCode;

import java.util.HashMap;

import static javax.swing.UIManager.put;

public class WeatherCategoryNotFoundException extends WeatherException {

    public WeatherCategoryNotFoundException(WeatherCategoryType type) {
        super(ErrorCode.WEATHER_CATEGORY_NOT_FOUND, new HashMap<>() {
            {
                put(type.name(), type.name());
            }
        });

    }
}
