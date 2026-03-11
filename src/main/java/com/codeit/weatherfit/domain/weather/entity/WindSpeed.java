package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record WindSpeed(
        double speed,
        AsWord asWord
) {
}
