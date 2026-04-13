package com.codeit.weatherfit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserLockUpdateRequest(
        @Schema(description = "잠금 상태", example = "true")
        @NotNull(message = "잠금 상태는 필수입니다.")
        Boolean locked
) {
}