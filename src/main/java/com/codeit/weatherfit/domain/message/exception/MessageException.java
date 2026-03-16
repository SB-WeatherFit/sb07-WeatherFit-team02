package com.codeit.weatherfit.domain.message.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

import java.util.Map;

public class MessageException extends WeatherFitException {
    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
