package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record ProfileLocationDto(
        @Schema(description = "위도") Double latitude,
        @Schema(description = "경도") Double longitude,
        @Schema(description = "격자 X좌표") Integer x,
        @Schema(description = "격자 Y좌표") Integer y,
        @Schema(description = "지역명 목록") List<String> locationNames
) {
    public static ProfileLocationDto from(Location location) {
        if (location == null) {
            return new ProfileLocationDto(null, null, null, null, List.of(""));
        }

        List<String> copiedLocationNames = location.getLocationNames() == null
                ? new ArrayList<>()
                : new ArrayList<>(location.getLocationNames());

        if (copiedLocationNames.isEmpty()) {
            copiedLocationNames.add("");
        }

        return new ProfileLocationDto(
                location.getLatitude(),
                location.getLongitude(),
                location.getX(),
                location.getY(),
                copiedLocationNames
        );
    }
}