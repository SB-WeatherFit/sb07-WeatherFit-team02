package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;

import java.util.UUID;

public interface FeedService {
    FeedDto create(FeedCreateRequest request);

    FeedGetResponse getFeedsByCursor(FeedGetRequest request);

    FeedDto update(UUID id, FeedUpdateRequest request);

    void delete(UUID id);
}
