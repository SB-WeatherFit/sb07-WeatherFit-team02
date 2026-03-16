package com.codeit.weatherfit.domain.message.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async("messageTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        try {
            messagingTemplate.convertAndSend(
                    "/sub/direct-messages_" + event.messageKey(),
                    event.content()
            );
        } catch (Exception e) {
            log.error("메시지 전송 실패: messageKey={}, error={}", event.messageKey(), e.getMessage());
        }
    }
}
