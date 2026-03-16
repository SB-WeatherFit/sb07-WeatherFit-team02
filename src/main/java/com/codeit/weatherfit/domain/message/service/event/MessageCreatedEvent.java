package com.codeit.weatherfit.domain.message.service.event;

public record MessageCreatedEvent(
        String messageKey,
        String content
) {
}
