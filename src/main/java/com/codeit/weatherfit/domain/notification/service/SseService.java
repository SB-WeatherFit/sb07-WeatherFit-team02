package com.codeit.weatherfit.domain.notification.service;

import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter connect(UUID userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        sseEmitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(userId));
        emitter.onTimeout(() -> sseEmitterRepository.delete(userId));
        emitter.onError(e -> sseEmitterRepository.delete(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("event")
                    .data("ping"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitterRepository.delete(userId);
        }

        return emitter;
    }

    public void send(NotificationDto notificationDto) {
        SseEmitter emitter = sseEmitterRepository.findByUserId(notificationDto.receiverId());

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .id(notificationDto.id().toString())
                    .name("notifications")
                    .data(notificationDto));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitterRepository.delete(notificationDto.receiverId());
        }
    }

    public Set<UUID> findSetUserIds(){
        return sseEmitterRepository.findAll().keySet();
    }

    public void broadcast(List<NotificationDto> notificationDtos) {
        for (NotificationDto notificationDto : notificationDtos) {
            send(notificationDto);
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanupExpiredEmitters() {
        Map<UUID, SseEmitter> allEmitters = sseEmitterRepository.findAll();

        allEmitters.forEach((userId, emitter) -> {
            try {
                // 아주 가벼운 하트비트 데이터 전송
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("check"));
            } catch (Exception e) {
                emitter.completeWithError(e);
                sseEmitterRepository.delete(userId);
            }
        });
    }
}