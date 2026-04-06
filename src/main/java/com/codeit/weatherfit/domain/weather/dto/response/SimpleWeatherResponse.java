package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.weather.entity.Precipitation;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.codeit.weatherfit.domain.weather.entity.Temperature;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.util.UUID;

public record SimpleWeatherResponse(

        UUID weatherId,
        SkyStatus skyStatus,
        PrecipitaionResponse precipitation,
        TemperatureResponse temperature
) {

    public static SimpleWeatherResponse from(Weather weather) {

        return new SimpleWeatherResponse(
                weather.getId(),
                weather.getSkyStatus(),
                PrecipitaionResponse.from(new Precipitation(weather.getType(), weather.getAmount(), weather.getProbability())),
                TemperatureResponse.from(new Temperature(weather.getTemperatureCurrent(), weather.getTemperatureComparedToDayBefore(), weather.getMin(), weather.getMax()))
        );
    }
}
