package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

public class FeedException extends WeatherFitException {
    public FeedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
