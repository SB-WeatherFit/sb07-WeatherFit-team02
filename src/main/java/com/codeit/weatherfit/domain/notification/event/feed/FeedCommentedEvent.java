package com.codeit.weatherfit.domain.notification.event.feed;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record FeedCommentedEvent(
        UUID receiverId,
        String commenterName,
        String commentContent
        )implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle(){
        return commenterName+"님이 내 피드를 좋아합니다.";
    }

    @Override
    public String getContent(){
        return commentContent;
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
