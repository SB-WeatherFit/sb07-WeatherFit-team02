package com.codeit.weatherfit.domain.auth.dto.response;

import com.codeit.weatherfit.domain.user.dto.response.UserDto;

public record JwtDto(
        UserDto userDto,
        String accessToken
) {
    public static JwtDto of(UserDto userDto, String accessToken) {
        return new JwtDto(userDto, accessToken);
    }
}