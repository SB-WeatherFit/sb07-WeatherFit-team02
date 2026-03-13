package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {

    private final MessageRepository messageRepository;


}
