package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
        UUID id,
        Instant createdAt,
        UUID feedId,
        UserSummary author,
        String content
) {
    public static CommentDto from(Comment comment, UserSummary author) {
        return new CommentDto(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getFeed().getId(),
                author,
                comment.getContent()
        );
    }
}
