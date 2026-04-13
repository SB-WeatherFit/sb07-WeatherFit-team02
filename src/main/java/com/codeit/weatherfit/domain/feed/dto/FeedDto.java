package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.WeatherSnapshot;
import com.codeit.weatherfit.domain.user.dto.response.UserFeedDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        @Schema(description = "피드 ID") UUID id,
        @Schema(description = "생성일시") Instant createdAt,
        @Schema(description = "수정일시") Instant updatedAt,
        @Schema(description = "작성자 정보") UserFeedDto author,
        @Schema(description = "날씨 스냅샷") WeatherSnapshot weather,
        @Schema(description = "착용 의상 목록") List<Ootd> ootds,
        @Schema(description = "피드 내용") String content,
        @Schema(description = "좋아요 수") Long likeCount,
        @Schema(description = "댓글 수") Long commentCount,
        @Schema(description = "내가 좋아요 했는지") boolean likedByMe
) {
    public static FeedDto from(
            Feed feed,
            List<Ootd> ootds,
            Long likeCount,
            Long commentCount,
            boolean likedByMe
    ) {
        return new FeedDto(
                feed.getId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                UserFeedDto.from(feed.getAuthor()),
                feed.getWeatherSnapshot(),
                ootds,
                feed.getContent(),
                likeCount,
                commentCount,
                likedByMe
        );
    }

}