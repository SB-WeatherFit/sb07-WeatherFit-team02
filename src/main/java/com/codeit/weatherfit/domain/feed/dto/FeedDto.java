package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UserDto author,
        Weather weather, // TODO 추후 WeatherDto로 수정
        List<FeedClothesDto> ootds,
        String content,
        Long likeCount,
        Long commentCount,
        boolean likedByMe
) {
    public static FeedDto from(Feed feed, List<FeedClothesDto> ootds, Long likeCount, Long commentCount, boolean likedByMe){
        return new FeedDto(
                feed.getId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                UserDto.from(feed.getAuthor()),
                feed.getWeather(),
                ootds,
                feed.getContent(),
                likeCount,
                commentCount,
                likedByMe
        );
    }
}
