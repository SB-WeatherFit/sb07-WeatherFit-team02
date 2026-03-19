package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageGetResponse;

public interface MessageService {
    void send(MessageCreateRequest request);
    MessageGetResponse getByCursor(MessageGetRequest request);
}
