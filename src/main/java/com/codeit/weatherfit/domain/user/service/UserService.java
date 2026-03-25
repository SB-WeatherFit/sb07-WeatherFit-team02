package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;
import com.codeit.weatherfit.domain.user.entity.User;

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

    void updatePassword(UUID userId, ChangePasswordRequest request);

    UserSummary getUserSummary(UUID userId);
    UserSummary getUserSummary(User user);
}