package com.codeit.weatherfit.domain.follow.service;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.user.entity.User;

public abstract class ProfileFixture {
    public static Profile createProfile(User user) {
        return Profile.create(user, null, null, null, null, "key");
    }
}
