package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Temperature {

    @Column(name = "temperature_current")
    private double current;

    @Column(name = "temperature_comparedToDayBefore")
    private double comparedToDayBefore;

    private double min;
    private double max;

    public Temperature(double current, double comparedToDayBefore, double min, double max) {
        this.current = current;
        this.comparedToDayBefore = comparedToDayBefore;
        this.min = min;
        this.max = max;
    }
}