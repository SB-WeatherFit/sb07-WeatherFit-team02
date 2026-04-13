package com.codeit.weatherfit.domain.profile.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "성별", enumAsRef = true)
public enum Gender {
    MALE, // 남성
    FEMALE, // 여성
    OTHER // 기타
}