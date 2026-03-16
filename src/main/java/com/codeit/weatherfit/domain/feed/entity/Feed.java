package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "feeds")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @JoinColumn(name = "weather_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Weather weather;

    @Column(nullable = false)
    private String content;

    public static Feed create(User author, Weather weather, String content) {
        Feed feed = new Feed();
        feed.author = author;
        feed.weather = weather;
        feed.content = content;
        return feed;
    }

}
