package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class FeedBadRequestException extends FeedException {
    public FeedBadRequestException() {
        super(ErrorCode.FEED_BAD_REQUEST);
    }
}
