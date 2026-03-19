package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
@Transactional
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void send() {
        User sender = userRepository.save(UserFixture.createUser());
        User receiver = userRepository.save(UserFixture.createUser("test2@gmail.com"));
        MessageCreateRequest request = new MessageCreateRequest(
                sender.getId(),
                receiver.getId(),
                "hello"
        );

        messageService.send(request);


        List<MessageCreatedEvent> events = applicationEvents.stream(MessageCreatedEvent.class)
                .toList();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().content()).isEqualTo("hello");
    }
}