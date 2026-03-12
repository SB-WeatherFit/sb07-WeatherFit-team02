package com.codeit.weatherfit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error","CM-001");

    final HttpStatus httpStatus;
    final String message;
    final String code;

    ErrorCode(HttpStatus httpStatus,String message,String code)
    {
        this.httpStatus=httpStatus;
        this.message=message;
        this.code=code;
    }
}
