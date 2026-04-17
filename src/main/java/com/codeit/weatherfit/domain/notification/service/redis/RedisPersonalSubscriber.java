package com.codeit.weatherfit.domain.notification.service.redis;

import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.notification.service.SseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPersonalSubscriber {

    private final SseService sseService;
    private final NotificationRepository notificationRepository; // 또는 Repository 직접 조회
    private final ObjectMapper objectMapper;

    public void onMessage(String json) {
        PersonalEvent event = null;
        try {
            event = objectMapper.readValue(json, PersonalEvent.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        if (!sseService.isConnected(event.receiverId())) {
            return;
        }

        Notification notification = notificationRepository.findById(event.notificationId()).orElseThrow(null);
        NotificationDto notificationDto = NotificationDto.create(notification);

        if(notificationDto.receiverId().equals(event.receiverId())) {
            return;
        }

        sseService.send(notificationDto);
        log.info("Sent notification receiverId: {} | event receiverId: {} ", notificationDto.receiverId(),event.receiverId());
    }
}
