package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;

public interface UserService {

    UserDto create(UserCreateRequest request);
}