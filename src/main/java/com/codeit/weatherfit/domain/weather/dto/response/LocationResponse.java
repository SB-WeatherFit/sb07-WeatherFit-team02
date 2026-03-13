package com.codeit.weatherfit.domain.weather.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;

public record LocationResponse(

        double latitude,
        double longitude,
        String address
) {

    public static LocationResponse from(Location location) {
        if (location == null) return null;
        return new LocationResponse(
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress()
        );
    }
}
