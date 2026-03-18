package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class AlreadyFollowException extends FollowException{
    public AlreadyFollowException() {
        super(ErrorCode.ALREADY_FOLLOW_EXCEPTION);
    }
}
