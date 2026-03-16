package com.codeit.weatherfit.domain.message.dto.response;

import java.util.UUID;

public record MessageUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
}
