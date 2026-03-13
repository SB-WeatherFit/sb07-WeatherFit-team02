package com.codeit.weatherfit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public enum ErrorCode {
    //Follow
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신을 팔로우할 수 없습니다.", "F-001"),
    INVALID_FOLLOW_ARGUMENT(HttpStatus.BAD_REQUEST, "팔로워와 팔로이는 반드시 존재해야 합니다.", "F-002"),

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
