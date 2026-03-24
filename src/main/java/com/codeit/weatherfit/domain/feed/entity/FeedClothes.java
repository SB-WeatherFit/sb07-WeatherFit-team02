package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feed_clothes")
public class FeedClothes extends BaseEntity { // FeedCoordinate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(nullable = false)
    private String name;

    private String imageKey;

    public static FeedClothes create(Feed feed, String name, String imageKey) {
        FeedClothes coordinate = new FeedClothes();
        coordinate.feed = feed;
        coordinate.name = name;
        coordinate.imageKey = imageKey;
        return coordinate;
    }
}
