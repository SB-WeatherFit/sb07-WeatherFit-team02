package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {
    Long countByFeed(Feed feed);
}
