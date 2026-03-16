package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageSocketController {

    private final MessageService messageService;

    @MessageMapping("/direct-messages_send")
    public void sendDirectMessage(MessageCreateRequest request) {
        messageService.send(request);
    }
}