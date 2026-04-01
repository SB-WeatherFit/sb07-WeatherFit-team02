package com.codeit.weatherfit.domain.message.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 카프카를 이용하기 위해 eventListener는 코드만 남겨두고 빈 등록하지 않도록 했습니다
 */
//@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async("messageTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        System.out.println("event.dmDto().messageId() = " + event.dmDto().id());
        try {
            messagingTemplate.convertAndSend(
                    "/sub/direct-messages_" + event.messageKey(),
                    event.dmDto()
            );
        } catch (Exception e) {
            log.error("메시지 전송 실패: messageKey={}, error={}", event.messageKey(), e.getMessage());
        }
    }
}
