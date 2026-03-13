package com.codeit.weatherfit.domain.message.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class NotSendSelfMessageException extends MessageException{
    public NotSendSelfMessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
