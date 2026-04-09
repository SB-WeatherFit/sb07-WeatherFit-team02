package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

@Table(name = "feeds")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT) // TODO Default 유저 세팅
    private User author;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private WeatherSnapshot weatherSnapshot;

    @Column(nullable = false)
    private String content;

    public static Feed create(User author, Weather weather, String content) {
        Feed feed = new Feed();
        feed.author = author;
        feed.content = content;
        feed.weatherSnapshot = WeatherSnapshot.from(weather);
        return feed;
    }

    public void update(String content) {
        this.content = content;
    }

}
