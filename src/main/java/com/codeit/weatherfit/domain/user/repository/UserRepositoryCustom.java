package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryCustom {

    List<User> searchUsers(UserSearchCondition condition);

    long countUsers(UserSearchCondition condition);

    List<UUID> getUserIdsByLocation(double longitude, double latitude);
}