package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;

import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest request);

    UserDtoCursorResponse getUsers(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            String sortDirection,
            String emailLike,
            String roleEqual,
            Boolean locked
    );

    UserDto updateRole(UUID userId, UserRoleUpdateRequest request);

    UserDto updateLock(UUID userId, UserLockUpdateRequest request);
}