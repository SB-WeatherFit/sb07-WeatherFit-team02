package com.codeit.weatherfit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}