package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageSocketControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private MessageService messageService;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @Test
    void send() throws ExecutionException, InterruptedException, TimeoutException {
        String url = "ws://localhost:" + port + "/ws";

        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .get(3, TimeUnit.SECONDS);

        UUID receiverId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
                receiverId,
                senderId,
                "안녕하세요"
        );


        session.send("/pub/direct-messages_send", request);

        verify(messageService, timeout(3000)).send(argThat(actual ->
                actual != null
                        && actual.receiverId().equals(receiverId)
                        && actual.senderId().equals(senderId)
                        && actual.content().equals("안녕하세요")
        ));
    }
}