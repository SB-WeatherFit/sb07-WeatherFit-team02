package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record LocationResponse(
        @Schema(description = "위도") double latitude,
        @Schema(description = "경도") double longitude,
        @Schema(description = "격자 X좌표") Integer x,
        @Schema(description = "격자 Y좌표") Integer y,
        @Schema(description = "지역명 목록") List<String> locationNames
) {

    public static LocationResponse from(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(
                location.getLatitude(),
                location.getLongitude(),
                location.getX(),
                location.getY(),
                location.getLocationNames()
        );
    }
}