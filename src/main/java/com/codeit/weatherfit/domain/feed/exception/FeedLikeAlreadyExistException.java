package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;

public class FeedLikeAlreadyExistException extends FeedException {
    public FeedLikeAlreadyExistException(Feed feed, User likeUser) {
        super(ErrorCode.ALREADY_LIKED);
    }
}
