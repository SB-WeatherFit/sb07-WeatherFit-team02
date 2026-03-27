package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class MessageControllerTest {

    private MessageService messageService;

    private MockMvcTester mvcTester;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messageService = Mockito.mock(MessageService.class);

        MessageController messageController = new MessageController(messageService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .build();

        mvcTester = MockMvcTester.create(mockMvc);
    }

    @Test
    void getMessageTest() throws Exception {
        when(messageService.searchMessages(any(), any()))
                .thenReturn(new MessageCursorResponse(null, null, null, false, 10));

        assertThat(
                mvcTester.get()
                        .uri("/api/direct-messages")
                        .param("userId", String.valueOf(UUID.randomUUID()))
                        .param("limit", "20"))
                .apply(print())
                .hasStatusOk();

        verify(messageService).searchMessages(any(), any());
    }
}