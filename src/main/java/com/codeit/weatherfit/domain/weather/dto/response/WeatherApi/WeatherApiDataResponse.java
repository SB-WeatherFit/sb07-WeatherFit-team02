package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.util.List;

public record WeatherApiDataResponse(

        @JsonProperty("dt_txt")
        String dtTxt,
        WeatherApiMainResponse main,
        List<WeatherApiWeatherResponse> weather,
        WeatherApiCloudResponse clouds,
        WeatherApiWindResponse wind,
        long visibility,
        @Nullable
        WeatherApiRainResponse rain
) {
}
