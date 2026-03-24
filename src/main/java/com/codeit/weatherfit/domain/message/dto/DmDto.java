package com.codeit.weatherfit.domain.message.dto;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record DmDto(
        UUID id,
        Instant createdAt,
        UserSummary sender,
        UserSummary receiver,
        String content
) {
    public static DmDto from(Message message, UserSummary sender, UserSummary receiver) {
        return new DmDto(
                message.getId(),
                message.getCreatedAt(),
                sender,
                receiver,
                message.getContent()
        );
    }
}
