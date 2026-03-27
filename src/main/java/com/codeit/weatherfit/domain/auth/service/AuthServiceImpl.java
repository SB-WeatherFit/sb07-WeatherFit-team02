package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;
import com.codeit.weatherfit.domain.auth.security.InMemoryAuthTokenStore;
import com.codeit.weatherfit.domain.auth.security.JwtTokenProvider;
import com.codeit.weatherfit.domain.auth.security.TemporaryPasswordGenerator;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryAuthTokenStore inMemoryAuthTokenStore;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final PasswordResetMailSender passwordResetMailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthTokenResult signIn(SignInRequest request) {
        validateSignInRequest(request);

        User user = userRepository.findByEmail(request.username())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.SIGN_IN_FAILED));

        if (user.isLocked()) {
            throw new WeatherFitException(ErrorCode.SIGN_IN_FAILED);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new WeatherFitException(ErrorCode.SIGN_IN_FAILED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        inMemoryAuthTokenStore.register(user.getId(), accessToken, refreshToken);

        JwtDto jwtDto = JwtDto.of(UserDto.from(user), accessToken);
        return AuthTokenResult.of(jwtDto, refreshToken);
    }

    @Override
    public void signOut(String authorizationHeader, String refreshToken) {
        String accessToken = extractAccessToken(authorizationHeader);

        if ((accessToken == null || accessToken.isBlank())
                && (refreshToken == null || refreshToken.isBlank())) {
            throw new WeatherFitException(ErrorCode.SIGN_OUT_FAILED);
        }

        inMemoryAuthTokenStore.revoke(accessToken, refreshToken);
    }

    @Override
    public AuthTokenResult refresh(String refreshToken) {
        validateRefreshToken(refreshToken);

        UUID userId = inMemoryAuthTokenStore.findUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            throw new WeatherFitException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (user.isLocked()) {
            throw new WeatherFitException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        inMemoryAuthTokenStore.revoke(null, refreshToken);
        inMemoryAuthTokenStore.register(user.getId(), newAccessToken, newRefreshToken);

        JwtDto jwtDto = JwtDto.of(UserDto.from(user), newAccessToken);
        return AuthTokenResult.of(jwtDto, newRefreshToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (request == null || request.email() == null || request.email().isBlank()) {
            throw new WeatherFitException(ErrorCode.INVALID_RESET_PASSWORD_REQUEST);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.RESET_PASSWORD_USER_NOT_FOUND));

        String temporaryPassword = temporaryPasswordGenerator.generate();
        String encodedTemporaryPassword = passwordEncoder.encode(temporaryPassword);

        user.updatePassword(encodedTemporaryPassword);

        passwordResetMailSender.send(user.getEmail(), temporaryPassword);
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

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new WeatherFitException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
            throw new WeatherFitException(ErrorCode.INVALID_REFRESH_TOKEN);
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