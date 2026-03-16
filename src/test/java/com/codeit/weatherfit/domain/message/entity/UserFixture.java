package com.codeit.weatherfit.domain.message.entity;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;

public class UserFixture {
    public static User create(){
        return User.create("test@email.com", "name", UserRole.USER, "password");
    }

    public static User create(String email){
        return User.create(email, "name", UserRole.USER, "password");
    }
}
