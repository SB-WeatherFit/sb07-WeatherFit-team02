package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.user.entity.User;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID messageId,
        Instant createdAt,
        MessageUser sender,
        MessageUser receiver,
        String content
) {
    public static MessageDto from(
            Message message,
            User sender,
            String senderProfileImageUrl,
            User receiver,
            String receiverProfileImageUrl) {
        MessageUser senderUser = MessageUser.from(sender, senderProfileImageUrl);
        MessageUser receiverUser = MessageUser.from(receiver, receiverProfileImageUrl);
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                senderUser,
                receiverUser,
                message.getContent()
        );
    }
}
