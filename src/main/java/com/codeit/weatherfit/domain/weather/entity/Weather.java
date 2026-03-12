package com.codeit.weatherfit.domain.weather.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.profile.entity.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Table(name = "weathers")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather extends BaseEntity {

    private Instant forecastedAt;
    private Instant forecastAt;
    @Embedded
    private Location location;
    @Embedded
    private Precipitation precipitation;
    private SkyStatus skyStatus;
    @Embedded
    private Humidity humidity;

    @Embedded
    private Temperature temperature;

    @Embedded
    private WindSpeed windSpeed;


}
