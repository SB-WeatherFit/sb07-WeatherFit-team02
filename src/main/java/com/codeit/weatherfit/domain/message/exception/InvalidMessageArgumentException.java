package com.codeit.weatherfit.domain.message.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class InvalidMessageArgumentException extends MessageException{
    public InvalidMessageArgumentException() {
        super(ErrorCode.INVALID_MESSAGE_ARGUMENT);
    }
}
