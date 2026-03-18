package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;
import com.codeit.weatherfit.domain.auth.security.InMemoryAuthTokenStore;
import com.codeit.weatherfit.domain.auth.security.JwtTokenProvider;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryAuthTokenStore inMemoryAuthTokenStore;

    @Override
    public JwtDto signIn(SignInRequest request) {
        validateSignInRequest(request);

        User user = userRepository.findByEmail(request.username())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.SIGN_IN_FAILED));

        if (user.isLocked()) {
            throw new WeatherFitException(ErrorCode.SIGN_IN_FAILED);
        }

        if (!user.getPassword().equals(request.password())) {
            throw new WeatherFitException(ErrorCode.SIGN_IN_FAILED);
        }

        // 임시 로그인
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        // 임시 로그인
        inMemoryAuthTokenStore.register(user.getId(), accessToken);

        return JwtDto.of(UserDto.from(user), accessToken);
    }

    @Override
    public void signOut(String authorizationHeader) {
        String accessToken = extractAccessToken(authorizationHeader);

        if (accessToken == null || accessToken.isBlank()) {
            throw new WeatherFitException(ErrorCode.SIGN_OUT_FAILED);
        }

        // 임시 로그인
        inMemoryAuthTokenStore.revoke(accessToken);
    }

    private void validateSignInRequest(SignInRequest request) {
        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.password() == null
                || request.password().isBlank()) {
            throw new WeatherFitException(ErrorCode.INVALID_SIGN_IN_REQUEST);
        }
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring(7);
    }
}