package com.codeit.weatherfit.domain.follow.entity;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;

public abstract class FollowFixture {

    public static Follow createFollow(){
        User user = User.create("test@gmail.com", "nickname", UserRole.USER, "password");
        User user2 = User.create("test2@gmail.com", "nickname2", UserRole.USER, "password");
        FollowCreateParam followCreateParam = new FollowCreateParam(user, user2);
        return Follow.create(followCreateParam);
    }
}
