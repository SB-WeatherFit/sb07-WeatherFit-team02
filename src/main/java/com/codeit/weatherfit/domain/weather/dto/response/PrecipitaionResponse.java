package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Precipitation;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;

public record PrecipitaionResponse(
        PrecipitationType type,
        double amount,
        double probability

) {
    public static PrecipitaionResponse from(Precipitation precipitation){
        if(precipitation== null) return null;
        return new PrecipitaionResponse(
                precipitation.getType(),
                precipitation.getAmount(),
                precipitation.getProbability()
        );
    }
}
