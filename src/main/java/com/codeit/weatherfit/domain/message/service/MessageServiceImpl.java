package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.DirectMessageDto;
import com.codeit.weatherfit.domain.message.dto.response.MessageGetResponse;
import com.codeit.weatherfit.domain.message.dto.response.SortBy;
import com.codeit.weatherfit.domain.message.dto.response.SortDirection;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final ProfileRepository profileRepository;

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

    @Override
    public MessageGetResponse getByCursor(MessageGetRequest request) {
        List<Message> messages = messageRepository.getByCursor(request);
        Message lastMessage = null;
        if (messages.size() == request.limit() + 1) {
            messages = messages.subList(0, request.limit());
            lastMessage = messages.getLast();
        }
        boolean hasNext = lastMessage != null;
        return new MessageGetResponse(
                messages.stream()
                        .map(m -> DirectMessageDto.from(
                                m,
                                getUserSummary(m.getSender()),
                                getUserSummary(m.getReceiver())

                        )).toList(),
                hasNext? lastMessage.getCreatedAt() : null,
                hasNext? lastMessage.getId() : null,
                hasNext,
                messages.size(),
                SortBy.createdAt,
                SortDirection.DESCENDING

        );

    }

    private UserSummary getUserSummary(User user) {
        return UserSummary.from(user,profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 프로필이 존재하지 않습니다."))); // TODO : 추후 커스텀 예외로);
    }

    private String generateDmKey(UUID senderId, UUID receiverId) {
        return Stream.of(senderId.toString(), receiverId.toString())
                .sorted()
                .collect(Collectors.joining("_"));
    }
}
