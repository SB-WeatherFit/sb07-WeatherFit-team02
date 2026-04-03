package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.*;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;

import java.util.UUID;

public interface FeedService {
    FeedDto create(FeedCreateRequest request, WeatherFitUserDetails userDetails);

    FeedGetResponse getFeedsByCursor(FeedGetRequest request, WeatherFitUserDetails userDetails);

    FeedDto update(UUID id, FeedUpdateRequest request, WeatherFitUserDetails userDetails);

    CommentDto createComment(UUID id, CommentCreateRequest request, WeatherFitUserDetails userDetails);

    void deleteComment(UUID id, UUID commentId, WeatherFitUserDetails userDetails);

    CommentGetResponse getCommentsByCursor(CommentGetRequest request, WeatherFitUserDetails userDetails);

    void delete(UUID id, WeatherFitUserDetails userDetails);

    void like(UUID id, WeatherFitUserDetails userDetails);

    void unlike(UUID id, WeatherFitUserDetails userDetails);
}
