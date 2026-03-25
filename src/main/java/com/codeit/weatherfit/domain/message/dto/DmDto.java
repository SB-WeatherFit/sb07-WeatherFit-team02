package com.codeit.weatherfit.domain.message.dto;

import com.codeit.weatherfit.domain.message.dto.response.MessageUser;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.time.Instant;
import java.util.UUID;

public record DmDto(
        UUID id,
        Instant createdAt,
        MessageUser sender,
        MessageUser receiver,
        String content
) {
    public static DmDto from(Message message, Profile senderProfile, Profile receiverProfile) {
        MessageUser sender = MessageUser.from(senderProfile);
        MessageUser receiver = MessageUser.from(receiverProfile);
        return new DmDto(
                message.getId(),
                message.getCreatedAt(),
                sender,
                receiver,
                message.getContent()
        );
    }
}
