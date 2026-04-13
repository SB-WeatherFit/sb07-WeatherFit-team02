package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

import java.util.UUID;

public class FeedForbiddenException extends FeedException {
    public FeedForbiddenException(UUID requestedId, UUID loginUserId) {
        super(ErrorCode.FEED_FORBIDDEN);
        this.getDetails().put("requestedId", requestedId);
        this.getDetails().put("loginUserId", loginUserId);
    }
}
