package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void send(MessageCreateRequest request) {
        UUID senderId = request.senderId();
        UUID receiverId = request.receiverId();
        String content = request.content();

        User sender = userRepository.findById(senderId)
                .orElseThrow(); // todo

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(); // todo

        Message message = Message.create(sender, receiver, content);
        messageRepository.save(message);

        String dmKey = generateDmKey(senderId, receiverId);
        eventPublisher.publishEvent(new MessageCreatedEvent(dmKey, content));
    }

    private String generateDmKey(UUID senderId, UUID receiverId) {
        return Stream.of(senderId.toString(), receiverId.toString())
                .sorted()
                .collect(Collectors.joining("_"));
    }
}
