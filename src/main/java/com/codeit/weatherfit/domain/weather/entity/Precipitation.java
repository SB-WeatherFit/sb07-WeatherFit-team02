package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Precipitation(

    PrecipitationType type,
    double amount,
    double probability
) {
}
