package com.codeit.weatherfit.domain.user.dto.request;

import com.codeit.weatherfit.domain.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(
        @NotNull(message = "권한은 필수입니다.")
        UserRole role
) {
}