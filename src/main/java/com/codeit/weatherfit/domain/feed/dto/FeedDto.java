package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        User author, // TODO 추후 UserDto로 수정
        Weather weather, // TODO 추후 WeatherDto로 수정
        List<Clothes> ootds, // 추후 List<OotdDto>로 수정
        String content,
        long likeCount,
        long commentCount,
        boolean likedByMe
) {
    public FeedDto from(Feed feed, List<Clothes> ootds, Long likeCount, Long commentCount, boolean likedByMe){
        return new FeedDto(
                feed.getId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                feed.getAuthor(),
                feed.getWeather(),
                ootds,
                feed.getContent(),
                likeCount,
                commentCount,
                likedByMe
        );
    }
}
