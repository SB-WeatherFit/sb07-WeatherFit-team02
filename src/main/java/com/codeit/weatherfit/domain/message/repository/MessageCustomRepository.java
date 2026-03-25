package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageCustomRepository {
    List<Message> searchMessages(MessageGetRequest request, UUID myId);
}
