package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

import java.util.Map;

public class ClothesException extends WeatherFitException {
    public ClothesException(ErrorCode errorCode) { super(errorCode); }
    public ClothesException(ErrorCode errorCode, Map<String, Object> details) { super(errorCode, details); }
}
