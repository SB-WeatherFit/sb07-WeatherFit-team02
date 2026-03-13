package com.codeit.weatherfit.domain.follow.dto.response;

import java.util.UUID;

public record FollowUser(
        UUID userId,
        String name,
        String profileImageUrl
) {
}
