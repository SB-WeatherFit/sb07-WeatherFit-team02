package com.codeit.weatherfit.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 역할", enumAsRef = true)
public enum UserRole {
    USER,
    ADMIN
}
