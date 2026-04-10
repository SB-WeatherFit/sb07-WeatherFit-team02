package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class FeedDocumentNotFoundException extends FeedException {
    public FeedDocumentNotFoundException(String feedId) {
        super(ErrorCode.FEED_DOCUMENT_NOT_EXIST);
        this.getDetails().put("feedId", feedId);
    }
}
