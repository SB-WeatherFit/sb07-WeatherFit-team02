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
        return "권한이 변경되었습니다";
    }

    @Override
    public String getContent() {
        return "확인해보세요";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return null;
    }
}
