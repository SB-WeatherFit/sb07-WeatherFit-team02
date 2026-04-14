package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {
    @Cacheable(value = "feedCommentCount", key = "#feed.id.toString()")
    Long countByFeed(Feed feed);

    void deleteByFeed(Feed feed);

    @Query("select c.feed.id, count(c) from Comment c " +
            "where c.feed.id in :feedIds " +
            "group by c.feed.id")
    List<Object[]> countByFeedIn(List<UUID> feedIds);
}
