package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.DmDto;
import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.dto.response.MessageDto;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.notification.event.message.MessageSentEvent;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.s3.S3Service;
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
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    @Transactional
    public void send(MessageCreateRequest request) {
        UUID senderId = request.senderId();
        UUID receiverId = request.receiverId();
        String content = request.content();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Message message = Message.create(sender, receiver, content);
        Message save = messageRepository.save(message);
        Profile receiverProfile = profileRepository.findWithUser(save.getReceiver().getId()).orElseThrow();
        Profile senderProfile = profileRepository.findWithUser(save.getSender().getId()).orElseThrow();
        DmDto messageDto = DmDto.from(save,
                senderProfile.getUser(),
                s3Service.getUrl(senderProfile.getProfileImageKey()),
                receiverProfile.getUser(),
                s3Service.getUrl(receiverProfile.getProfileImageKey())
        );


        String dmKey = generateDmKey(senderId, receiverId);
        eventPublisher.publishEvent(new MessageCreatedEvent(dmKey, messageDto));
        eventPublisher.publishEvent(new MessageSentEvent(receiverId, sender.getName(), save.getContent()));
    }

    @Override
    public MessageCursorResponse searchMessages(MessageGetRequest request, UUID myId) {
        List<Message> messages = messageRepository.searchMessages(request, myId);
        Profile myProfile = profileRepository.findWithUser(request.userId()).orElseThrow();
        Profile theirProfile = profileRepository.findWithUser(myId).orElseThrow();
        long totalCount = messageRepository.countMessage(myProfile.getUser().getId(), theirProfile.getUser().getId());

        boolean hasNext = false;
        if (messages.size() == request.limit() + 1) {
            messages = messages.subList(0, request.limit());
            hasNext = true;
        }

        List<MessageDto> data = messages.stream()
                .map(message -> {
                    boolean isMySend = message.getSender().getId().equals(myId);
                            return MessageDto.from(
                                    message,
                                    message.getSender(),
                                    s3Service.getUrl(isMySend ? myProfile.getProfileImageKey() : theirProfile.getProfileImageKey()),
                                    message.getReceiver(),
                                    s3Service.getUrl(isMySend ? theirProfile.getProfileImageKey() : myProfile.getProfileImageKey())
                            );
                        }
                )
                .toList();

        return new MessageCursorResponse(
                data,
                hasNext ? data.getLast().createdAt() : null,
                hasNext ? data.getLast().messageId() : null,
                hasNext,
                totalCount
        );

    }

    private String generateDmKey(UUID senderId, UUID receiverId) {
        return Stream.of(senderId.toString(), receiverId.toString())
                .sorted()
                .collect(Collectors.joining("_"));
    }
}
