package com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi;

import java.util.List;

public record WeatherAdministrationItem(
    List<WeatherAdministrationTime> item
) {
}
