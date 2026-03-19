package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;

public record AuthTokenResult(
        JwtDto jwtDto,
        String refreshToken
) {
    public static AuthTokenResult of(JwtDto jwtDto, String refreshToken) {
        return new AuthTokenResult(jwtDto, refreshToken);
    }
}