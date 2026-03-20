package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.dto.request.CommentGetRequest;
import com.codeit.weatherfit.domain.feed.entity.Comment;

import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> getCommentsByCursor(CommentGetRequest request);
}
