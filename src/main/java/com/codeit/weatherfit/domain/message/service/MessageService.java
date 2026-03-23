package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;

import java.util.UUID;

public interface MessageService {
    void send(MessageCreateRequest request);
    MessageCursorResponse searchMessages(MessageGetRequest request, UUID myId);
}
