package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.weather.entity.Precipitation;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PrecipitaionResponse(
        @Schema(description = "강수 유형") PrecipitationType type,
        @Schema(description = "강수량") double amount,
        @Schema(description = "강수 확률") double probability

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
