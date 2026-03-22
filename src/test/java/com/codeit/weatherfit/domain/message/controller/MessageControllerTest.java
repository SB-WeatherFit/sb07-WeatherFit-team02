package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageControllerTest {

    @Autowired
    private MessageService messageService;
}