package com.codeit.weatherfit.domain.message.entity;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;

public class UserFixture {
    public static User createUser(){
        return User.create("test@email.com", "name", UserRole.USER, "password");
    }

    public static User createUser(String email){
        return User.create(email, "name", UserRole.USER, "password");
    }
}
