package com.codeit.weatherfit.domain.notification.event.feed;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record FeedLikedEvent(
        UUID receiverId,
        String likerName,
        String feedContent
        )implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle(){
        return likerName+"님이 내 피드를 좋아합니다.";
    }

    @Override
    public String getContent(){
        return feedContent;
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
