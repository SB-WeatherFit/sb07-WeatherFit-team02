package com.codeit.weatherfit.domain.message.service.event;

import com.codeit.weatherfit.domain.message.dto.DmDto;
public record MessageCreatedEvent(
        String messageKey,
        DmDto messageDto
) {
}
