package com.codeit.weatherfit.domain.follow.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class NotExistFollowException extends FollowException {
    public NotExistFollowException() {
        super(ErrorCode.NOT_EXIST_FOLLOW);
    }
}
