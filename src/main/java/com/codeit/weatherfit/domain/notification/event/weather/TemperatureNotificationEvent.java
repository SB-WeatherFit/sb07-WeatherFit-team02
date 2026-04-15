package com.codeit.weatherfit.domain.notification.event.weather;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record TemperatureNotificationEvent(
        UUID receiverId,
        String content
)  implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle() {
        return "급격한 온도 변화 주의보";
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
