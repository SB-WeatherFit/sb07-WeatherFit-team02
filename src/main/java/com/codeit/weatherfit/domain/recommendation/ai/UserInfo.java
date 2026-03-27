package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.profile.entity.Profile;

import java.time.LocalDate;

public record UserInfo(
    String gender,                // 성별 (MALE, FEMALE 등)
    int temperatureSensitivity,   // 온도 민감도
    int age                       // 나이 (생년월일 기반 계산, 스타일 결정 요인)
) {
    public static UserInfo from(Profile profile) {
        // 나이 계산 (BirthDate가 있다면 추가, 없다면 생략 가능)
        Integer age = (profile.getBirthDate() != null)
                ? LocalDate.now().getYear() - profile.getBirthDate().getYear() + 1
                : -1; // 혹은 -1

        return new UserInfo(
            profile.getGender().name(),
            profile.getTemperatureSensitivity(),
                age
        );
    }
}