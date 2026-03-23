package com.codeit.weatherfit.domain.notification.event.feed;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record NewFeedFromFollowingsEvent(
        UUID receiverId,
        String followeeName,
        String feedContent
        )implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle(){
        return followeeName+"님이 새로운 피드를 작성했어요.";
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
