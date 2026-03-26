package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String name,
        String gender,
        LocalDate birthDate,
        ProfileLocationDto location,
        Integer temperatureSensitivity,
        String profileImageUrl
) {
    public static ProfileDto from(Profile profile, String profileImageUrl) {
        return new ProfileDto(
                profile.getUser().getId(),
                profile.getUser().getName(),
                profile.getGender().name(),
                profile.getBirthDate(),
                ProfileLocationDto.from(profile.getLocation()),
                profile.getTemperatureSensitivity(),
                profileImageUrl
        );
    }
}