package com.codeit.weatherfit.domain.feed.event;

import java.util.UUID;

public record FeedUpdatedEvent(
        UUID feedId,
        EventType eventType,
        String content
) {
    public static FeedUpdatedEvent liked(UUID feedId) {
        return new FeedUpdatedEvent(feedId, EventType.LIKE_UP, null);
    }

    public static FeedUpdatedEvent unliked(UUID feedId) {
        return new FeedUpdatedEvent(feedId, EventType.LIKE_DOWN, null);
    }

    public static FeedUpdatedEvent contentUpdated(UUID feedId, String content) {
        return new FeedUpdatedEvent(feedId, EventType.CONTENT_UPDATED, content);
    }
    public enum EventType {
        LIKE_UP,
        LIKE_DOWN,
        CONTENT_UPDATED
    }
}
