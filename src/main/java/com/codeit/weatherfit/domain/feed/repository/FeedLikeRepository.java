package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.FeedLike;
import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {

    @Cacheable(value = "feedLikeCount", key= "#feed.id.toString()")
    Long countByFeed(Feed feed);

    boolean existsByFeedAndLikedUser(Feed feed, User likedUser);

    void deleteByFeedAndLikedUser(Feed feed, User likedUser);

    @Query("select fl.feed.id, count(fl) from FeedLike fl " +
            "where fl.feed.id in :feedIds " +
            "group by fl.feed.id")
    List<Object[]> countByFeedIn(@Param("feedIds") List<UUID> feedIds);

    @Query("select fl.feed.id from FeedLike fl " +
            "where fl.feed.id in :feedIds and fl.likedUser = :user")
    Set<UUID> findLikedFeedIds(@Param("feedIds") List<UUID> feedIds, @Param("user") User user);
}