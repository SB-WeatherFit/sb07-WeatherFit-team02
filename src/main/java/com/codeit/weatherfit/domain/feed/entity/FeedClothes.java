package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feed_clothes")
public class FeedClothes extends BaseEntity { // FeedCoordinate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothes_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Clothes clothes;

    public static FeedClothes create(Feed feed, Clothes clothes) {
        FeedClothes coordinate = new FeedClothes();
        coordinate.feed = feed;
        coordinate.clothes = clothes;
        return coordinate;
    }
}
