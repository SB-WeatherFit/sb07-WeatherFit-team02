package com.codeit.weatherfit.domain.message.service.kafka;

import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "message.send",
            groupId = "message-send-group-#{T(java.util.UUID).randomUUID().toString()}",
            containerFactory = "messageKafkaListenerContainerFactory"
    )
    public void consume(MessageCreatedEvent event) {
        log.info("카프카에서 메시지 수신: messageId={}, key={}", event.dmDto().id(), event.messageKey());

        try {
            messagingTemplate.convertAndSend(
                    "/sub/direct-messages_" + event.messageKey(),
                    event.dmDto()
            );
        } catch (Exception e) {
            log.error("웹소켓 전송 실패: messageKey={}, error={}", event.messageKey(), e.getMessage());
        }
    }
}
