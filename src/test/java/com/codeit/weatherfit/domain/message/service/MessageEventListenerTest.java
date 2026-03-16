package com.codeit.weatherfit.domain.message.event;

import com.codeit.weatherfit.domain.message.entity.MessageCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class MessageEventListenerTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @MockitoBean
    SimpMessagingTemplate messagingTemplate;

    @Test
    void send() {
        MessageCreatedEvent event = new MessageCreatedEvent(
                "89a71b30-e73f-415f-b791-e8cb9694e5b6_96671e32-ff27-4215-bf96-d0575abc11f4",
                "안녕하세요"
        );

        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> eventPublisher.publishEvent(event));

        verify(messagingTemplate).convertAndSend(
                "/sub/direct-messages_" + event.dmKey(),
                event.content()
        );
    }

    @Test
    void sendFail() {
        MessageCreatedEvent event = new MessageCreatedEvent(
                "89a71b30-e73f-415f-b791-e8cb9694e5b6_96671e32-ff27-4215-bf96-d0575abc11f4",
                "안녕하세요"
        );

        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        assertThatThrownBy(() ->
                tx.executeWithoutResult(status -> {
                    eventPublisher.publishEvent(event);
                    throw new RuntimeException("강제 롤백");
                })
        ).isInstanceOf(RuntimeException.class);

        verifyNoInteractions(messagingTemplate);
    }
}