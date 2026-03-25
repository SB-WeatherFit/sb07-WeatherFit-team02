package com.codeit.weatherfit.domain.notification.event.message;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record MessageSentEvent(
        UUID receiverId,
        String senderName,
        String content
        )implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle(){
        return senderName+"님이 메시지을 보냈어요";
    }

    @Override
    public String getContent(){
        return content;
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
