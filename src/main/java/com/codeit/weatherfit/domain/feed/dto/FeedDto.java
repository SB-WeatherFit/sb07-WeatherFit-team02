package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.feed.entity.WeatherSnapshot;
import com.codeit.weatherfit.domain.user.dto.response.UserFeedDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UserFeedDto author,
        WeatherSnapshot weather,
        List<Ootd> ootds,
        String content,
        Long likeCount,
        Long commentCount,
        boolean likedByMe
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