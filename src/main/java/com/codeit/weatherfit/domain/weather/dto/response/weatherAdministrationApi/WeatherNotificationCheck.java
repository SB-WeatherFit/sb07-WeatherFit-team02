package com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi;

import java.time.Instant;

public record WeatherNotificationCheck(
        Double deltaTemperature,
        Double deltaPrecipitation,
        Instant targetTime
) {
}
