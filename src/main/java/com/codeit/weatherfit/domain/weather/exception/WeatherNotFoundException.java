package com.codeit.weatherfit.domain.weather.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

import java.util.HashMap;
import java.util.UUID;

public class WeatherNotFoundException extends WeatherException{

    public WeatherNotFoundException(UUID id) {
        super(ErrorCode.WEATHER_NOT_FOUND, new HashMap<>(){
            {
                put("id",id);
            }
        });
    }

    public WeatherNotFoundException() {
        super(ErrorCode.WEATHER_NOT_FOUND, new HashMap<>()) ;
    }
}
