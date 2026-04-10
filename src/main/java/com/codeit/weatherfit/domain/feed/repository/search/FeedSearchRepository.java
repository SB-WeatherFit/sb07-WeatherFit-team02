package com.codeit.weatherfit.domain.feed.repository.search;

import com.codeit.weatherfit.domain.feed.entity.search.FeedDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface FeedSearchRepository extends ElasticsearchRepository<FeedDocument, UUID> {
    void deleteByFeedId(UUID feedId);
}
