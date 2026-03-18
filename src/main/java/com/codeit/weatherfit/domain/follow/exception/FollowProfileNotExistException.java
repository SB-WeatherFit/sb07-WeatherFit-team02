package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class FollowProfileNotExistException extends FollowException {
    public FollowProfileNotExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
