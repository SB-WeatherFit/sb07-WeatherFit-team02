package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationCustomRepository {
    List<Notification> searchCursor(NotificationSearchCondition condition);
}
