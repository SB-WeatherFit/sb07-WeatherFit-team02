package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Humidity {

    private double current;
    private double comparedToDayBefore;

    public Humidity(double current, double comparedToDayBefore) {
        this.current = current;
        this.comparedToDayBefore = comparedToDayBefore;
    }
}