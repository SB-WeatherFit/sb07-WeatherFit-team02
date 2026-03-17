package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class InvalidFollowArgumentException extends FollowException{
    public InvalidFollowArgumentException() {
        super(ErrorCode.INVALID_FOLLOW_ARGUMENT);
    }
}
