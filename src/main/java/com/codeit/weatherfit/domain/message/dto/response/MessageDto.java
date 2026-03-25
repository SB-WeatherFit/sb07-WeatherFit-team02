package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID messageId,
        Instant createdAt,
        MessageUser sender,
        MessageUser receiver,
        String content
) {
    public static MessageDto from(Message message, Profile senderProfile, Profile receiverProfile) {
        MessageUser sender = MessageUser.from(senderProfile);
        MessageUser receiver = MessageUser.from(receiverProfile);
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                sender,
                receiver,
                message.getContent()
        );
    }
}
