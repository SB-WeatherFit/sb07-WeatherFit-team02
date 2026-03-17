package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class FollowUserNotExistException extends FollowException {
    public FollowUserNotExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
