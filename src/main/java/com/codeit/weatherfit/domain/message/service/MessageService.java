package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;

public interface MessageService {
    void send(MessageCreateRequest request);
}
