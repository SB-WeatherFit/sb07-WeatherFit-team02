package com.codeit.weatherfit.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInRequest(
        @Schema(description = "사용자 이메일", example = "user@example.com")
        String username,
        @Schema(description = "비밀번호", example = "password123")
        String password
) {
}