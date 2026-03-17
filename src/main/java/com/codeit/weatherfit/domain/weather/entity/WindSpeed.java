package com.codeit.weatherfit.domain.weather.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class WindSpeed {

    private double speed;
    private AsWord asWord;

    public WindSpeed(AsWord asWord,double speed) {
        this.speed = speed;
        this.asWord = asWord;

    }
}