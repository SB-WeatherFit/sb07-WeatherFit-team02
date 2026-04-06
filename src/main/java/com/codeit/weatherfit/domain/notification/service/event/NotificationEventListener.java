package com.codeit.weatherfit.domain.notification.service.event;

import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;
import com.codeit.weatherfit.domain.notification.event.SystemNotificationEvent;
import com.codeit.weatherfit.domain.notification.service.NotificationService;
import com.codeit.weatherfit.domain.notification.service.redis.PersonalEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageCreated(PersonalNotificationEvent event) {
        NotificationDto notificationDto = notificationService.send(
                event.getReceiverId(),
                event.getTitle(),
                event.getContent(),
                event.getNotificationLevel()
        );

        PersonalEvent personalEvent = new PersonalEvent(notificationDto.receiverId(), notificationDto.id());
        try {
            String json = objectMapper.writeValueAsString(personalEvent);
            redisTemplate.convertAndSend("notification.personal",  json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(SystemNotificationEvent event) {

        UUID groupId = notificationService.broadcast(
                event.getTitle(),
                event.getContent(),
                event.getNotificationLevel());

        redisTemplate.convertAndSend("notification.broadcast", groupId.toString());
    }
}