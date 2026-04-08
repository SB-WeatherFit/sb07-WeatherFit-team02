package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedClothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedClothesRepository extends JpaRepository<FeedClothes, UUID> {
    List<FeedClothes> findAllByFeed(Feed feed);

    @Query(value = "select fc.clothes_snapshot->>'imageKey' from feed_clothes fc " +
            "where fc.clothes_snapshot->>'imageKey' is not null",
            nativeQuery = true)
    List<String> findAllImageKeys();
}
