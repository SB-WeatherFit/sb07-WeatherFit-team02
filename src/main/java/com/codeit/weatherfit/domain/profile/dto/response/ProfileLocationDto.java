package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;

import java.util.List;

public record ProfileLocationDto(
        Double latitude,
        Double longitude,
        Integer x,
        Integer y,
        List<String> locationNames
) {
    public static ProfileLocationDto from(Location location) {
        if (location == null) {
            return new ProfileLocationDto(null, null, null, null, List.of());
        }

        return new ProfileLocationDto(
                location.getLatitude(),
                location.getLongitude(),
                location.getX(),
                location.getY(),
                location.getLocationNames()
        );
    }
}