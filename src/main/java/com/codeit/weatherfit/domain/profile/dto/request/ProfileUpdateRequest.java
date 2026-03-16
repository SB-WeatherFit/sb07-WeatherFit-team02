package com.codeit.weatherfit.domain.profile.dto.request;

import com.codeit.weatherfit.domain.profile.entity.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ProfileUpdateRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        LocalDate birthDate,

        @Valid
        @NotNull(message = "위치 정보는 필수입니다.")
        LocationRequest location,

        @NotNull(message = "온도 민감도는 필수입니다.")
        @Min(value = 1, message = "온도 민감도는 1 이상이어야 합니다.")
        @Max(value = 5, message = "온도 민감도는 5 이하여야 합니다.")
        Integer temperatureSensitivity
) {
    public record LocationRequest(
            Double latitude,
            Double longitude,
            Integer x,
            Integer y,
            List<String> locationNames
    ) {
    }
}