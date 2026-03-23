package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.profile.entity.Gender;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class ProfileFixture {

    public static Profile createProfile(User user) {
        Location location = Location.create(0.0, 0.0, 0, 0, new ArrayList<>());
        return Profile.create(user, Gender.FEMALE, LocalDate.now(), location, 3, null);
    }
}
