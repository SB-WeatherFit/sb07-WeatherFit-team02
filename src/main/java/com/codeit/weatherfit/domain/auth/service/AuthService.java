package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;

public interface AuthService {

    JwtDto signIn(SignInRequest request);

    void signOut(String authorizationHeader);
}