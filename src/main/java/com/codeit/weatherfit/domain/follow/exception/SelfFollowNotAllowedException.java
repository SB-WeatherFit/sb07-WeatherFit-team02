package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class SelfFollowNotAllowedException extends FollowException{
    public SelfFollowNotAllowedException() {
        super(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }
}
