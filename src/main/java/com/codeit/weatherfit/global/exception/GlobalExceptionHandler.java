package com.codeit.weatherfit.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> restExceptionHandler(Exception e){

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        Map<String,Object> details = new HashMap<>();
        details.put("code",errorCode.code);
        ErrorResponse errorResponse = new ErrorResponse(
                e.getClass().getSimpleName(),
                errorCode.message,
                details

        );
        return ResponseEntity.status(errorCode.httpStatus)
                .body(errorResponse);

    }
}
