package com.codeit.weatherfit.domain.weather.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

import java.util.Map;

public class WeatherException extends WeatherFitException {

    public WeatherException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public WeatherException(ErrorCode errorCode) {
        super(errorCode);
    }
}
