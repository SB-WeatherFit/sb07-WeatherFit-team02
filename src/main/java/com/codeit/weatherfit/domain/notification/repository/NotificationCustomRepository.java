package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationsSearchCondition;
import com.codeit.weatherfit.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationCustomRepository {
    List<Notification> searchCursor(NotificationsSearchCondition condition);
}
