package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedClothesRepository extends JpaRepository<FeedClothes, UUID> {
    List<FeedClothes> findAllByFeed(Feed feed);
}
