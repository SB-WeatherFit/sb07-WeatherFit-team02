package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Humidity(
        double current,
        double comparedToDayBefore
) {
}
