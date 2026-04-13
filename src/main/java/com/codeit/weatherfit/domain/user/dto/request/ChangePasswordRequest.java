package com.codeit.weatherfit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @Schema(description = "새 비밀번호", example = "newPassword123")
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        String password
) {
}