package com.codeit.weatherfit.domain.message.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DirectMessageDto(
        UUID messageId,
        Instant createdAt,
        MessageUser sender,
        MessageUser receiver,
        String content
) {
}
