package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String name,
        String gender,
        LocalDate birthDate,
        LocationDto location,
        Integer temperatureSensitivity,
        String profileImageUrl
) {
    public static ProfileDto from(Profile profile) {
        return new ProfileDto(
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getGender().name(),
                profile.getBirthDate(),
                LocationDto.from(profile.getLocation()),
                profile.getTemperatureSensitivity(),
                profile.getProfileImageUrl()
        );
    }

    public record LocationDto(
            Double latitude,
            Double longitude,
            Integer x,
            Integer y,
            List<String> locationNames
    ) {
        public static LocationDto from(Location location) {
            if (location == null) {
                return new LocationDto(null, null, null, null, List.of());
            }

            return new LocationDto(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getX(),
                    location.getY(),
                    location.getLocationNames()
            );
        }
    }
}