package com.codeit.weatherfit.domain.weather.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.dto.response.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Table(name = "weathers")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather extends BaseEntity {

    @Column(name = "forecasted_at")
    private Instant forecastedAt;

    @Column(name = "forecast_at")
    private Instant forecastAt;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "as_word", nullable = false)
    private AsWord asWord;

    @Column(name = "amount")
    private double amount;

    @Column(name = "compared_to_day_before")
    private double comparedToDayBefore;

    @Column(name= "current")
    private double current;

    @Column(name = "max")
    private double max;

    @Column(name = "min")
    private double min;

    @Column(name = "probability")
    private double probability;

    @Column(name = "speed")
    private double speed;

    @Column(name = "temperature_compared_to_day_before")
    private double temperatureComparedToDayBefore;

    @Column(name = "temperature_current")
    private double temperatureCurrent;

    @Column(name="address_first")
    private String addressFirst;

    @Column(name="address_second")
    private String addressSecond;

    @Column(name="address_third")
    private String addressThird;

    @Enumerated(EnumType.STRING)
    @Column(name="sky_status")
    private SkyStatus skyStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private PrecipitationType type;

    public static Weather create(Temperature temperature,
                                 WindSpeed windSpeed,
                                 Precipitation precipitation,
                                 SkyStatus skyStatus,
                                 Humidity humidity,
                                 Instant forecastedAt,
                                 Instant forecastAt,
                                 Location location
    ) {
        Weather newWeather = new Weather();

        //windSpeed
        newWeather.speed = windSpeed.getSpeed();
        newWeather.asWord = windSpeed.getAsWord();

        //temperature
        newWeather.temperatureCurrent = temperature.getCurrent();
        newWeather.max = temperature.getMax();
        newWeather.min = temperature.getMin();
        newWeather.temperatureComparedToDayBefore = temperature.getComparedToDayBefore();

        //humidity
        newWeather.current = humidity.getCurrent();
        newWeather.comparedToDayBefore = humidity.getComparedToDayBefore();

        //precipitation
        newWeather.amount = precipitation.getAmount();
        newWeather.probability = precipitation.getProbability();
        newWeather.type = precipitation.getType();


        newWeather.skyStatus = skyStatus;

        //location
        newWeather.latitude = location.getLatitude();
        newWeather.longitude = location.getLongitude();
        newWeather.addressFirst = location.getLocationNames().get(0);
        newWeather.addressSecond = location.getLocationNames().get(1);
        newWeather.addressThird= location.getLocationNames().get(2);

        newWeather.forecastedAt = forecastedAt.truncatedTo(ChronoUnit.MICROS);
        newWeather.forecastAt = forecastAt.truncatedTo(ChronoUnit.MICROS);
        return newWeather;

    }

    public static Weather create(
            WeatherResponse dto
    ){

        Weather newWeather = new Weather();
        //windspeed
        newWeather.speed = dto.windSpeed().speed();
        newWeather.asWord = dto.windSpeed().asWord();

        //temperature
        newWeather.temperatureCurrent = dto.temperature().current();
        newWeather.max = dto.temperature().max();
        newWeather.min = dto.temperature().min();
        newWeather.temperatureComparedToDayBefore = dto.temperature().comparedToDayBefore();

        //humidity
        newWeather.current = dto.humidity().current();
        newWeather.comparedToDayBefore = dto.humidity().comparedTodayBefore();

        //precipitation
        newWeather.amount = dto.precipitation().amount();
        newWeather.probability = dto.precipitation().probability();
        newWeather.type = dto.precipitation().type();

        newWeather.skyStatus = dto.skyStatus();
        //location
        newWeather.latitude = dto.location().latitude();
        newWeather.longitude = dto.location().longitude();
        newWeather.addressFirst = dto.location().locationNames().get(0);
        newWeather.addressSecond = dto.location().locationNames().get(1);
        newWeather.addressThird= dto.location().locationNames().get(2);
        newWeather.forecastedAt = dto.forecastedAt().truncatedTo(ChronoUnit.MICROS);
        newWeather.forecastAt = dto.forecastAt().truncatedTo(ChronoUnit.MICROS);
        return newWeather;

    }


}
