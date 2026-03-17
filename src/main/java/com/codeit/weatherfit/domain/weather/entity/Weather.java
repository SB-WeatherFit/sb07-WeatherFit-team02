package com.codeit.weatherfit.domain.weather.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.dto.response.*;
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

        newWeather.location = location;
        newWeather.humidity = humidity;
        newWeather.temperature = temperature;
        newWeather.skyStatus = skyStatus;
        newWeather.precipitation = precipitation;
        newWeather.windSpeed = windSpeed;
        newWeather.forecastedAt = forecastedAt;
        newWeather.forecastAt = forecastAt;
        return newWeather;

    }

    public static Weather create(
            WeatherResponse dto
    ){
        Weather newWeather = new Weather();
        newWeather.location = Location.create(
                dto.location().latitude(),
                dto.location().longitude(),
                dto.location().x(),
                dto.location().y(),
                dto.location().locationNames()
        );

        newWeather.humidity = new Humidity(

                dto.humidity().current(),
                dto.humidity().comparedTodayBefore()
        );
        newWeather.temperature = new  Temperature(
                dto.temperature().current(),
                dto.temperature().comparedToDayBefore(),
                dto.temperature().min(),
                dto.temperature().max()
        );
        newWeather.skyStatus = dto.skyStatus();
        newWeather.precipitation = new Precipitation(
                dto.precipitation().type(),
                dto.precipitation().amount(),
                dto.precipitation().probability()
        );
        newWeather.windSpeed = new WindSpeed(
                dto.windSpeed().asWord(),
                dto.windSpeed().speed()

        );
        newWeather.forecastedAt = dto.forecastedAt();
        newWeather.forecastAt = dto.forecastAt();
        return newWeather;
    }


}
