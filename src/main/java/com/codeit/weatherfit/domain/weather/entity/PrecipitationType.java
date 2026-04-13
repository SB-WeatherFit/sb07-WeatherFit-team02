package com.codeit.weatherfit.domain.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강수 유형", enumAsRef = true)
public enum PrecipitationType {

    NONE("맑음"),
    RAIN("비"),
    RAIN_SNOW("진눈깨비"),
    SNOW("눈"),
    SHOWER("소나기")
    ;
    public final String description;
    PrecipitationType(String description) {
        this.description = description;
    }
}
