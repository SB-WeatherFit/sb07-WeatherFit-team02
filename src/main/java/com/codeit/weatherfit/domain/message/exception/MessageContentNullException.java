package com.codeit.weatherfit.domain.message.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class MessageContentNullException extends MessageException{

    public MessageContentNullException() {
        super(ErrorCode.MESSAGE_CONTENT_NULL);
    }
}
