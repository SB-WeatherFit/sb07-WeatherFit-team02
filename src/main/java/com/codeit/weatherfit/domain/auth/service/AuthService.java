package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;

public interface AuthService {

    AuthTokenResult signIn(SignInRequest request);

    void signOut(String authorizationHeader, String refreshToken);

    AuthTokenResult refresh(String refreshToken);

    void resetPassword(ResetPasswordRequest request);
}