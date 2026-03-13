package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class SelfFollowNotAllowedException extends FollowException{
    public SelfFollowNotAllowedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
