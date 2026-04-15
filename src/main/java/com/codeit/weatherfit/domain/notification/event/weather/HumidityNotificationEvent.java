package com.codeit.weatherfit.domain.notification.event.weather;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record HumidityNotificationEvent(
        UUID receiverId,
        String content
)  implements PersonalNotificationEvent {
    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle() {
        return "급격한 호우 주의보 발령";
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.WARNING;
    }
}
