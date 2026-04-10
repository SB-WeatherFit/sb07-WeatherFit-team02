package com.codeit.weatherfit.domain.feed.entity.search;

import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "feeds")
@Getter
public class FeedDocument {

    @Id()
    private UUID feedId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;

    @Field(type = FieldType.Keyword)
    private UUID authorId;

    @Field(type = FieldType.Keyword)
    private SkyStatus skyStatus;

    @Field(type = FieldType.Keyword)
    private PrecipitationType precipitationType;

    @Field(type = FieldType.Long)
    private long likeCount;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    public static FeedDocument from(Feed feed, long likeCount) {
        FeedDocument doc = new FeedDocument();
        doc.feedId = feed.getId();
        doc.content = feed.getContent();
        doc.authorId = feed.getAuthor().getId();
        doc.skyStatus = feed.getWeatherSnapshot().skyStatus();
        doc.precipitationType = feed.getWeatherSnapshot().type();
        doc.likeCount = likeCount;
        doc.createdAt = feed.getCreatedAt();
        return doc;
    }

    public void liked() {
        this.likeCount++;
    }

    public void unliked() {
        this.likeCount--;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
