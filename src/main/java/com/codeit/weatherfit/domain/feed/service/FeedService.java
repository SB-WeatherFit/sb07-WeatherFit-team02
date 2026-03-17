package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface FeedService {
    FeedDto create(FeedCreateRequest requestDto);
    FeedDto findById(UUID id);
    List<FeedDto> findAllByUserId(UUID userId);
    FeedDto update(UUID id, FeedUpdateRequest requestDto);
    void delete(UUID id);
}
