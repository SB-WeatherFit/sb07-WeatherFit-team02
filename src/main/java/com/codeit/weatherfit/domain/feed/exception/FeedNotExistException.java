package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

import java.util.UUID;

public class FeedNotExistException extends FeedException {
    public FeedNotExistException(UUID id) {
        super(ErrorCode.FEED_NOT_EXIST);
        this.getDetails().put("id", id);

    }
}
