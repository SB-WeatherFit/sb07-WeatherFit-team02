package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.service.MessageService;
import com.codeit.weatherfit.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(MessageController.class)
@Import(SecurityConfig.class)
class MessageControllerTest {

    @Autowired
    MockMvcTester mvcTester;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;


    @Test
    void getMessageTest() throws Exception {
        when(messageService.searchMessages(any(), any()))
                .thenReturn(new MessageCursorResponse(null, null, null, false, 10));

        assertThat(mvcTester.get().uri("/api/direct-messages")
                .param("userId", String.valueOf(UUID.randomUUID())) // @ModelAttribute는 파라미터로!
                .param("limit", "20")
        )
                .apply(print())
                .hasStatusOk();

        verify(messageService).searchMessages(any(), any());
    }
}