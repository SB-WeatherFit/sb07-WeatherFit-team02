package com.codeit.weatherfit.domain.notification.service.redis;

import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.notification.service.SseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisBroadcastSubscriber {

    private final SseService sseService;
    private final NotificationRepository notificationRepository; // 또는 Repository 직접 조회
    private final ObjectMapper objectMapper;

    public void onMessage(String json) {

        UUID groupId = UUID.fromString(json);

        Set<UUID> localUserIds = sseService.findSetUserIds();

        if (localUserIds.isEmpty()) {
            return;
        }

        List<Notification> myNotifications = notificationRepository
                .findAllByGroupIdAndReceiverIdIn(groupId, localUserIds);

        for (Notification notification : myNotifications) {
            sseService.send(NotificationDto.create(notification));
        }
    }
}
