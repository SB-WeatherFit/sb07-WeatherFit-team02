package com.codeit.weatherfit.domain.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "하늘 상태", enumAsRef = true)
public enum SkyStatus {
    CLEAR,
    MOSTLY_CLOUDY,
    CLOUDY
}
