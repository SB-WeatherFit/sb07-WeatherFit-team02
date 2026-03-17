package com.codeit.weatherfit.domain.follow.entity;

import com.codeit.weatherfit.domain.user.entity.User;

public record FollowCreateParam(
        User followee,
        User follower
) {
}
