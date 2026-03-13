package com.codeit.weatherfit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public enum ErrorCode {
    //Follow
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신을 팔로우할 수 없습니다.", "F-001"),
    INVALID_FOLLOW_ARGUMENT(HttpStatus.BAD_REQUEST, "팔로워와 팔로이는 반드시 존재해야 합니다.", "F-002"),

    //Message
    NOT_SEND_SELF_MESSAGE(HttpStatus.BAD_REQUEST, "자신에게 메시지를 보낼 수 없습니다", "M-001"),
    INVALID_MESSAGE_ARGUMENT(HttpStatus.BAD_REQUEST, "발신자와 수신자는 반드시 존재해야 합니다.", "M-002"),
    MESSAGE_CONTENT_NULL(HttpStatus.BAD_REQUEST, "메세지의 내용을 정해주세요", "M-003"),

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
