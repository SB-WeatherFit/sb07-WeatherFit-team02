package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Temperature(
        @Column(name = "temperature_current")
        double current,
        @Column(name ="temperature_comparedToDayBefore")
        double comparedToDayBefore ,
        double min,
        double max
) {
}
