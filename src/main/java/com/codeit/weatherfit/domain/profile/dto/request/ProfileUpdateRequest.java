package com.codeit.weatherfit.domain.profile.dto.request;

import com.codeit.weatherfit.domain.profile.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ProfileUpdateRequest(
        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @Schema(description = "성별", example = "MALE")
        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @Schema(description = "생년월일", example = "1995-03-15")
        LocalDate birthDate,

        @Schema(description = "위치 정보")
        @Valid
        LocationRequest location,

        @Schema(description = "온도 민감도 (1~5)", example = "3")
        @NotNull(message = "온도 민감도는 필수입니다.")
        @Min(value = 1, message = "온도 민감도는 1 이상이어야 합니다.")
        @Max(value = 5, message = "온도 민감도는 5 이하여야 합니다.")
        Integer temperatureSensitivity
) {
    public record LocationRequest(
            @Schema(description = "위도", example = "37.5665")
            Double latitude,
            @Schema(description = "경도", example = "126.9780")
            Double longitude,
            @Schema(description = "격자 X좌표", example = "60")
            Integer x,
            @Schema(description = "격자 Y좌표", example = "127")
            Integer y,
            @Schema(description = "지역명 목록")
            List<String> locationNames
    ) {
    }
}