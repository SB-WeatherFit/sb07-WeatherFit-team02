package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedLike;
import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {

    @Cacheable(value = "feedLikeCount", key= "#feed.id.toString()")
    Long countByFeed(Feed feed);

    boolean existsByFeedAndLikedUser(Feed feed, User likedUser);

    void deleteByFeedAndLikedUser(Feed feed, User likedUser);
}
