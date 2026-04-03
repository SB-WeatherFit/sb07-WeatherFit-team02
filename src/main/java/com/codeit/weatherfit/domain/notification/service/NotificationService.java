package com.codeit.weatherfit.domain.notification.service;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;

import java.util.Set;
import java.util.UUID;

public interface NotificationService {
    NotificationDto send(UUID receiverId, String title, String content, NotificationLevel level);

    UUID broadcast(String title, String content, NotificationLevel level);

    NotificationCursorResponse search(NotificationSearchCondition condition, UUID userId);

    void delete(UUID notificationId);
}
