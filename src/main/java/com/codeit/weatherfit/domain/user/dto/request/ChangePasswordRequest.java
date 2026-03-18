package com.codeit.weatherfit.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        String password
) {
}