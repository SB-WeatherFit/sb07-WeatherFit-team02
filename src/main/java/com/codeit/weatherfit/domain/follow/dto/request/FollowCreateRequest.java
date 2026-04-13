package com.codeit.weatherfit.domain.follow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FollowCreateRequest(
        @Schema(description = "팔로우할 대상 ID", example = "123e4567-e89b-12d3-a456-426614174000") @NotNull UUID followeeId,
        @Schema(description = "팔로우 요청자 ID", example = "123e4567-e89b-12d3-a456-426614174001") @NotNull UUID followerId
) {
}
