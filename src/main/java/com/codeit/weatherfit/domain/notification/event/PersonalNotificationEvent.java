package com.codeit.weatherfit.domain.notification.event;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;

import java.util.UUID;

public interface PersonalNotificationEvent {
    UUID getReceiverId();

    String getTitle();

    String getContent();

    NotificationLevel getNotificationLevel();
}
