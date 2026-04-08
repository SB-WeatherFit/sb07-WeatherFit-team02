package com.codeit.weatherfit.domain.user.event;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;
import com.codeit.weatherfit.domain.user.entity.UserRole;

import java.util.UUID;

public record UserRoleChangedEvent(
        UUID receiverId,
        UserRole beforeRole,
        UserRole afterRole
) implements PersonalNotificationEvent {

    @Override
    public UUID getReceiverId() {
        return receiverId;
    }

    @Override
    public String getTitle() {
        return "권한이 변경되었습니다";
    }

    @Override
    public String getContent() {
        return "회원님의 권한이 " + beforeRole + "에서 " + afterRole + "(으)로 변경되었습니다.";
    }

    @Override
    public NotificationLevel getNotificationLevel() {
        return NotificationLevel.INFO;
    }
}