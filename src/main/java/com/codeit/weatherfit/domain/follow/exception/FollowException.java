package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

import java.util.Map;

public class FollowException extends WeatherFitException {
    public FollowException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FollowException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
