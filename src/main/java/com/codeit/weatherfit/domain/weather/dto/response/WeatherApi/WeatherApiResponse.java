package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

import java.util.List;

public record WeatherApiResponse(
        List<WeatherApiDataResponse> list
) {
}
