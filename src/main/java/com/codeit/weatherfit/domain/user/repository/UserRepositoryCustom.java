package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.user.entity.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> searchUsers(UserSearchCondition condition);

    long countUsers(UserSearchCondition condition);
}