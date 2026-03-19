package com.codeit.weatherfit.domain.notification.event.clothes;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.SystemNotificationEvent;

public record AttributeDeletedEvent(String attributeName) implements SystemNotificationEvent {

    @Override
    public String getTitle() {
        return "의상 속성이 삭제되었어요.";
    }

    @Override
    public String getContent() {
        return "["+attributeName+"] 속성이 삭제되었어요.";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
