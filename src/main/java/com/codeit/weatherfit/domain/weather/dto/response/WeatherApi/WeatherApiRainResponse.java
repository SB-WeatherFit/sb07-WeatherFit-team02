package com.codeit.weatherfit.domain.weather.dto.response.WeatherApi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherApiRainResponse(

        @JsonProperty("3h")
        double rainVolume

) {
}
