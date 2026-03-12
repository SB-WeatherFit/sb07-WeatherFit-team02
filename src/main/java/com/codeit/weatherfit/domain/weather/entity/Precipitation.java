package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Precipitation {

    private PrecipitationType type;
    private double amount;
    private double probability;

    public Precipitation(PrecipitationType type, double amount, double probability) {
        this.type = type;
        this.amount = amount;
        this.probability = probability;
    }
}