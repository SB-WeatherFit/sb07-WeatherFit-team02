package com.codeit.weatherfit.domain.auth.dto.request;

public record SignInRequest(
        String username,
        String password
) {
}