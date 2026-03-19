package com.codeit.weatherfit.domain.notification.event.follow;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record FollowerCreatedEvent(
        UUID receiverId,
        String followerName
        )implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle(){
        return followerName+"님이 나를 팔로우했어요.";
    }

    @Override
    public String getContent(){
        return "";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
