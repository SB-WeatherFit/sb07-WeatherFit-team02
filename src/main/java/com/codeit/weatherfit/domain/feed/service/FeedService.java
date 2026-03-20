package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;

import java.util.UUID;

public interface FeedService {
    FeedDto create(FeedCreateRequest request);

    FeedGetResponse getFeedsByCursor(FeedGetRequest request);

    FeedDto update(UUID id, FeedUpdateRequest request);

    CommentDto createComment(CommentCreateRequest request);

    CommentGetResponse getCommentsByCursor(CommentGetRequest request);

    void delete(UUID id);
}
