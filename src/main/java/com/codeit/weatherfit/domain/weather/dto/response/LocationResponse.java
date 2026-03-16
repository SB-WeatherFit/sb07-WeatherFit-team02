package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;

import java.util.List;

public record LocationResponse(
        double latitude,
        double longitude,
        Integer x,
        Integer y,
        List<String> locationNames
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