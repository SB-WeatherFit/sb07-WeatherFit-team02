package com.codeit.weatherfit.domain.message.entity;

public record MessageCreatedEvent(
        String dmKey,
        String content
) {
}
