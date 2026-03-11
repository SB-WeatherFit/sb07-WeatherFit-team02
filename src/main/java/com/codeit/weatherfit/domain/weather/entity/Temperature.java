package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Temperature(
        double current,
        double comparedToDayBefore,
        double min,
        double max
) {
}
