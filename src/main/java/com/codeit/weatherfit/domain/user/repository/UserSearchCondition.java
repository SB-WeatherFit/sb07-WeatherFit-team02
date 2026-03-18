package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.user.entity.UserRole;

import java.util.UUID;

public record UserSearchCondition(
        String cursor,
        UUID idAfter,
        int limit,
        String sortBy,
        String sortDirection,
        String emailLike,
        UserRole roleEqual,
        Boolean locked
) {
}