package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.weather.dto.response.PrecipitaionResponse;
import com.codeit.weatherfit.domain.weather.dto.response.TemperatureResponse;
import com.codeit.weatherfit.domain.weather.entity.Precipitation;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.codeit.weatherfit.domain.weather.entity.Temperature;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UserDto author,
        WeatherDto weather,
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
                WeatherDto.from(feed.getWeather()),
                ootds,
                feed.getContent(),
                likeCount,
                commentCount,
                likedByMe
        );
    }

    // TODO 충접 dto 각 도메인으로 옮기기

    public record WeatherDto(
            UUID weatherId,
            SkyStatus skyStatus,
            PrecipitaionResponse precipitation,
            TemperatureResponse temperature
    ){
        public static WeatherDto from(Weather weather) {
            return new WeatherDto(
                    weather.getId(),
                    weather.getSkyStatus(),
                    PrecipitaionResponse.from(new Precipitation(weather.getType(), weather.getAmount(), weather.getProbability())),
                    TemperatureResponse.from(new Temperature(weather.getTemperatureCurrent(), weather.getTemperatureComparedToDayBefore(), weather.getMin(), weather.getMax()))
            );
        }
    }
}
