package com.codeit.weatherfit.domain.profile.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Table(name = "profiles")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @OneToOne
    private User user;
    private Gender gender;
    private LocalDate birthDate;

    @Embedded
    private Location location;

    private int temperatureSensitivity;
    private String profileImageUrl;

}


