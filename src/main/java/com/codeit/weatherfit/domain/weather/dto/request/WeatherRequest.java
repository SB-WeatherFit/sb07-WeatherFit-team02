package com.codeit.weatherfit.domain.weather.dto.request;

import lombok.EqualsAndHashCode;

public record WeatherRequest(
        double longitude,
        double latitude
) {
}
