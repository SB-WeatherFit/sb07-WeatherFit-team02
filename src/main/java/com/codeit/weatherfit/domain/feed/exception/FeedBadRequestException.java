package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class FeedBadRequestException extends FeedException {
    public FeedBadRequestException() {
        super(ErrorCode.FEED_BAD_REQUEST);
    }

    public FeedBadRequestException(String message) {
        super(ErrorCode.FEED_BAD_REQUEST);
        this.getDetails().put("message", message);
    }
}
