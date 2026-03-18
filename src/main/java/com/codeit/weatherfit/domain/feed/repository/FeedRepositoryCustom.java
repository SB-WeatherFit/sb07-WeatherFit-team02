package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.entity.Feed;

import java.util.List;

public interface FeedRepositoryCustom {
    List<Feed> findWithCursor(FeedGetRequest request);

}
