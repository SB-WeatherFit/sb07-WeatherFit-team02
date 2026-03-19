package com.codeit.weatherfit.domain.notification.event.clothes;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.SystemNotificationEvent;

public record AttributeAddedEvent(String attributeName) implements SystemNotificationEvent {

    @Override
    public String getTitle() {
        return "새로운 의상 속성이 추가되었어요.";
    }

    @Override
    public String getContent() {
        return "내 의상에 ["+attributeName+"] 속성을 추가해보세요.";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}
