package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;

public class FeedLikeNotExistException extends FeedException {
    public FeedLikeNotExistException(Feed feed, User likeUser) {
        super(ErrorCode.LIKE_NOT_EXIST);
    }
}
