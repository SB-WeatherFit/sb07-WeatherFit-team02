package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID messageId,
        Instant createdAt,
        UserSummary sender,
        UserSummary receiver,
        String content
) {
    public static MessageDto from(Message message, UserSummary sender, UserSummary receiver) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                sender,
                receiver,
                message.getContent()
        );
    }
}
