package com.codeit.weatherfit.domain.profile.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Table(name = "profiles")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Embedded
    private Location location;

    @Column(name = "temperature_sensitivity", nullable = false)
    private int temperatureSensitivity;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    private Profile(
            User user,
            Gender gender,
            LocalDate birthDate,
            Location location,
            int temperatureSensitivity,
            String profileImageKey
    ) {
        validateUser(user);
        validateGender(gender);
        validateTemperatureSensitivity(temperatureSensitivity);

        this.user = user;
        this.gender = gender;
        this.birthDate = birthDate;
        this.location = location;
        this.temperatureSensitivity = temperatureSensitivity;
        this.profileImageKey = profileImageKey;
    }

    public static Profile create(
            User user,
            Gender gender,
            LocalDate birthDate,
            Location location,
            Integer temperatureSensitivity,
            String profileImageUrl
    ) {
        return new Profile(
                user,
                gender == null ? Gender.OTHER : gender,
                birthDate,
                location == null ? Location.empty() : location,
                temperatureSensitivity == null ? 3 : temperatureSensitivity,
                profileImageUrl
        );
    }

    public void updateGender(Gender gender) {
        validateGender(gender);
        this.gender = gender;
    }

    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void updateLocation(Location location) {
        this.location = location == null ? this.location : location;
    }

    public void updateTemperatureSensitivity(int temperatureSensitivity) {
        validateTemperatureSensitivity(temperatureSensitivity);
        this.temperatureSensitivity = temperatureSensitivity;
    }

    public void updateProfileImageKey(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }

    private static void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new WeatherFitException(ErrorCode.INVALID_PROFILE_USER);
        }
    }

    private static void validateGender(Gender gender) {
        if (Objects.isNull(gender)) {
            throw new WeatherFitException(ErrorCode.INVALID_PROFILE_GENDER);
        }
    }

    private static void validateTemperatureSensitivity(int temperatureSensitivity) {
        if (temperatureSensitivity < 1 || temperatureSensitivity > 5) {
            throw new WeatherFitException(ErrorCode.INVALID_PROFILE_TEMPERATURE_SENSITIVITY);
        }
    }
}