package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;

import java.util.ArrayList;
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