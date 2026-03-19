package com.codeit.weatherfit.domain.notification.event.clothes;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.SystemNotificationEvent;

public record AttributeChangedEvent(String attributeName) implements SystemNotificationEvent {

    @Override
    public String getTitle() {
        return "의상 속성이 변경되었어요.";
    }

    @Override
    public String getContent() {
        return "["+attributeName+"] 속성을 확인해보세요.";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
