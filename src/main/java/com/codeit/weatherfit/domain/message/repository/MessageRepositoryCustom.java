package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.entity.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getByCursor(MessageGetRequest request);
}
