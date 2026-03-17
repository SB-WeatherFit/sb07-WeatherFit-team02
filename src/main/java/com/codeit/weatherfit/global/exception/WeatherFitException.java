package com.codeit.weatherfit.global.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class WeatherFitException extends RuntimeException {

    final ErrorCode errorCode;
    final Map<String, Object> details;

    public WeatherFitException(ErrorCode errorCode, Map<String, Object> details) {
        details.put("timestamp", Instant.now());
        this.errorCode = errorCode;
        this.details = details;
    }

    public WeatherFitException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("timestamp", Instant.now());
        this.details = tmp;
    }
}
