package com.codeit.weatherfit.domain.notification.event.user;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;

import java.util.UUID;

public record RoleChangedEvent (UUID receiverId) implements PersonalNotificationEvent {
    @Override
    public UUID getReceiverId() {
        return null;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getContent() {
        return "";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return null;
    }
}
