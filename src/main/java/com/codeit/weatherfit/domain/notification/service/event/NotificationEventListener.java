package com.codeit.weatherfit.domain.notification.service.event;

import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.event.PersonalNotificationEvent;
import com.codeit.weatherfit.domain.notification.event.SystemNotificationEvent;
import com.codeit.weatherfit.domain.notification.service.NotificationService;
import com.codeit.weatherfit.domain.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService notificationService;
    private final SseService sseService;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageCreated(PersonalNotificationEvent event) {
        NotificationDto notificationDto = notificationService.send(
                event.getReceiverId(),
                event.getTitle(),
                event.getContent(),
                event.getNotificationLevel()
        );

        sseService.send(notificationDto);
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(SystemNotificationEvent event) {

        Set<UUID> setUserIds = sseService.findSetUserIds();

        List<NotificationDto> notificationDtos = notificationService.broadcast(
                event.getTitle(),
                event.getContent(),
                event.getNotificationLevel(),
                setUserIds);

        sseService.broadcast(notificationDtos);
    }
}