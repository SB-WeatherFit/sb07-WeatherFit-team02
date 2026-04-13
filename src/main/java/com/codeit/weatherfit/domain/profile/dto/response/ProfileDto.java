package com.codeit.weatherfit.domain.profile.dto.response;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileDto(
        @Schema(description = "사용자 ID") UUID userId,
        @Schema(description = "이름") String name,
        @Schema(description = "성별", allowableValues = {"MALE", "FEMALE", "OTHER"}) String gender,
        @Schema(description = "생년월일") LocalDate birthDate,
        @Schema(description = "위치 정보") ProfileLocationDto location,
        @Schema(description = "온도 민감도") Integer temperatureSensitivity,
        @Schema(description = "프로필 이미지 URL") String profileImageUrl
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