package com.codeit.weatherfit.domain.message.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
        UUID receiverId,
        UUID senderId,
        String content
) {
}