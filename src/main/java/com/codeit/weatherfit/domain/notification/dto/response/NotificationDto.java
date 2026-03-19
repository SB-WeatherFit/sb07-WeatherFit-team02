package com.codeit.weatherfit.domain.notification.dto.response;

import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        Instant createdAt,
        UUID receiverId,
        String title,
        String content,
        NotificationLevel level
) {
    public static NotificationDto create(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getReceiver().getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getLevel()
        );
    }
}
