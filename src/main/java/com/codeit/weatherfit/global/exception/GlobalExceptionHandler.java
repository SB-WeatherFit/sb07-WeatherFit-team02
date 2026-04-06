package com.codeit.weatherfit.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WeatherFitException.class)
    public ResponseEntity<ErrorResponse> handleWeatherFitException(WeatherFitException e) {
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> details = new HashMap<>(e.getDetails());
        details.put("code", errorCode.getCode());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getClass().getSimpleName(),
                errorCode.getMessage(),
                details
        );

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> restExceptionHandler(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        Map<String, Object> details = new HashMap<>();
        details.put("code", errorCode.getCode());
        details.put("message", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getClass().getSimpleName(),
                errorCode.getMessage(),
                details
        );

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}