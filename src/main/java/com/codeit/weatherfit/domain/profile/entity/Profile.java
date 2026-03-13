package com.codeit.weatherfit.domain.profile.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
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

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private Profile(
            User user,
            Gender gender,
            LocalDate birthDate,
            Location location,
            int temperatureSensitivity,
            String profileImageUrl
    ) {
        validateUser(user);
        validateGender(gender);
        validateTemperatureSensitivity(temperatureSensitivity);

        this.user = user;
        this.gender = gender;
        this.birthDate = birthDate;
        this.location = location == null ? Location.empty() : location;
        this.temperatureSensitivity = temperatureSensitivity;
        this.profileImageUrl = profileImageUrl;
    }

    public static Profile create(
            User user,
            Gender gender,
            LocalDate birthDate,
            Location location,
            int temperatureSensitivity,
            String profileImageUrl
    ) {
        return new Profile(
                user,
                gender,
                birthDate,
                location,
                temperatureSensitivity,
                profileImageUrl
        );
    }

    public static Profile createEmpty(User user) {
        return new Profile(
                user,
                Gender.OTHER,
                null,
                Location.empty(),
                3,
                null
        );
    }

    public void update(
            Gender gender,
            LocalDate birthDate,
            Location location,
            int temperatureSensitivity,
            String profileImageUrl
    ) {
        validateGender(gender);
        validateTemperatureSensitivity(temperatureSensitivity);

        this.gender = gender;
        this.birthDate = birthDate;
        this.location = location == null ? Location.empty() : location;
        this.temperatureSensitivity = temperatureSensitivity;
        this.profileImageUrl = profileImageUrl;
    }

    private static void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("user must not be null");
        }
    }

    private static void validateGender(Gender gender) {
        if (Objects.isNull(gender)) {
            throw new IllegalArgumentException("gender must not be null");
        }
    }

    private static void validateTemperatureSensitivity(int temperatureSensitivity) {
        if (temperatureSensitivity < 1 || temperatureSensitivity > 5) {
            throw new IllegalArgumentException("temperatureSensitivity must be between 1 and 5");
        }
    }
}