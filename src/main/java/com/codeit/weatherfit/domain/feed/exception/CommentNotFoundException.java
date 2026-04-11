package com.codeit.weatherfit.domain.feed.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

import java.util.UUID;

public class CommentNotFoundException extends FeedException {
    public CommentNotFoundException(UUID commentId) {
        super(ErrorCode.COMMENT_NOT_FOUND);
        this.getDetails().put("commentId", commentId);
    }
}
