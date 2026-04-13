package com.codeit.weatherfit.domain.user.dto.request;

import com.codeit.weatherfit.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(
        @Schema(description = "사용자 역할", example = "ADMIN")
        @NotNull(message = "권한은 필수입니다.")
        UserRole role
) {
}