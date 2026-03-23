package com.codeit.weatherfit.domain.notification.event;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;

public interface SystemNotificationEvent {
    String getTitle();

    String getContent();

    NotificationLevel getNotificationLevel();
}
