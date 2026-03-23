package com.codeit.weatherfit.domain.message.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        @NotNull UUID receiverId,
        @NotNull UUID senderId,
        @NotNull String content
) {
}