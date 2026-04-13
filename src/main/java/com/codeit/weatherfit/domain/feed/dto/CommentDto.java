package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
        @Schema(description = "댓글 ID") UUID id,
        @Schema(description = "생성일시") Instant createdAt,
        @Schema(description = "피드 ID") UUID feedId,
        @Schema(description = "작성자 정보") UserSummary author,
        @Schema(description = "댓글 내용") String content
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
