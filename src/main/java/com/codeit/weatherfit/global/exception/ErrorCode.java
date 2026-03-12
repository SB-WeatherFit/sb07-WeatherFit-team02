package com.codeit.weatherfit.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error","CM-001");

    String code;
    String message;
    HttpStatus httpStatus;
    ErrorCode(HttpStatus httpStatus,String message,String code)
    {
        this.httpStatus=httpStatus;
        this.message=message;
        this.code=code;
    }
}
