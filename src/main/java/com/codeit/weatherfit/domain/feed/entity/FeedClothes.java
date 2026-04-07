package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feed_clothes")
public class FeedClothes extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private ClothesSnapshot clothesSnapshot;

    public static FeedClothes create(Feed feed, Clothes clothes, List<String> attributes) {
        FeedClothes coordinate = new FeedClothes();
        coordinate.feed = feed;
        coordinate.clothes = clothes;
        coordinate.clothesSnapshot = ClothesSnapshot.from(clothes, attributes);
        return coordinate;
    }
}
