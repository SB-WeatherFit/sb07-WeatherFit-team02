package com.codeit.weatherfit.domain.feed.service;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;

import java.util.List;
import java.util.UUID;

public interface FeedService {
    FeedDto create(FeedCreateRequestDto requestDto);
    FeedDto findById(UUID id);
    List<FeedDto> findAllByUserId(UUID userId);
}
