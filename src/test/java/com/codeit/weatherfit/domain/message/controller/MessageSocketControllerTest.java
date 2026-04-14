package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.feed.repository.search.FeedSearchRepository;
import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @MockitoBean
    private FeedSearchRepository feedSearchRepository;

    @BeforeEach
    void setUp() {
//        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        //  SockJsClient를 생성합니다.
        SockJsClient sockJsClient = new SockJsClient(transports);

        // StompClient가 SockJsClient를 사용하도록 설정합니다.
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // 부트4에선 다른 걸 사용
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

    @Test
    void connectFail(){
        String url = "ws://localhost:" + port + "/ws";

        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders();
        wsHeaders.add("Origin", "http://another-domain.com"); // 다른 도메인 설정

        assertThatThrownBy(()-> stompClient.connectAsync(
                url,
                wsHeaders,
                (StompHeaders) null,
                new StompSessionHandlerAdapter() {
                }
        ).get(3, TimeUnit.SECONDS))
        .isInstanceOf(Exception.class);
    }
}