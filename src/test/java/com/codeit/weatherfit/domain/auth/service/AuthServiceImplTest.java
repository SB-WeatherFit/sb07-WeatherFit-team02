package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.security.InMemoryAuthTokenStore;
import com.codeit.weatherfit.domain.auth.security.JwtTokenProvider;
import com.codeit.weatherfit.domain.auth.security.TemporaryPasswordGenerator;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private InMemoryAuthTokenStore inMemoryAuthTokenStore;

    @Mock
    private TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Mock
    private PasswordResetMailSender passwordResetMailSender;

    @InjectMocks
    private AuthServiceImpl authService;

    @Nested
    class SignInTest {

        @Test
        @DisplayName("로그인에 성공한다")
        void signIn() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(jwtTokenProvider.generateAccessToken(user)).thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken(user)).thenReturn("refresh-token");

            AuthTokenResult result = authService.signIn(new SignInRequest("test3@test.com", "password"));

            assertThat(result.jwtDto().userDto().email()).isEqualTo("test3@test.com");
            assertThat(result.jwtDto().accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");

            verify(inMemoryAuthTokenStore, times(1))
                    .register(user.getId(), "access-token", "refresh-token");
        }

        @Test
        @DisplayName("로그인 요청값이 잘못되면 예외가 발생한다")
        void signInFailWhenRequestInvalid() {
            assertThatThrownBy(() -> authService.signIn(new SignInRequest("", "")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_SIGN_IN_REQUEST);
        }

        @Test
        @DisplayName("사용자가 없으면 로그인에 실패한다")
        void signInFailWhenUserNotFound() {
            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.signIn(new SignInRequest("test3@test.com", "password")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.SIGN_IN_FAILED);
        }

        @Test
        @DisplayName("비밀번호가 다르면 로그인에 실패한다")
        void signInFailWhenPasswordMismatch() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.signIn(new SignInRequest("test3@test.com", "wrong-password")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.SIGN_IN_FAILED);
        }
    }

    @Nested
    class SignOutTest {

        @Test
        @DisplayName("로그아웃에 성공한다")
        void signOut() {
            authService.signOut("Bearer access-token", "refresh-token");

            verify(inMemoryAuthTokenStore, times(1))
                    .revoke("access-token", "refresh-token");
        }

        @Test
        @DisplayName("로그아웃 토큰이 없으면 실패한다")
        void signOutFailWhenTokensMissing() {
            assertThatThrownBy(() -> authService.signOut(null, null))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.SIGN_OUT_FAILED);
        }
    }

    @Nested
    class RefreshTest {

        @Test
        @DisplayName("리프레시에 성공한다")
        void refresh() {
            UUID userId = UUID.randomUUID();
            User user = User.create("test3@test.com", "test3", UserRole.USER, "password");

            when(inMemoryAuthTokenStore.findUserIdByRefreshToken("refresh-token")).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtTokenProvider.generateAccessToken(user)).thenReturn("new-access-token");
            when(jwtTokenProvider.generateRefreshToken(user)).thenReturn("new-refresh-token");

            AuthTokenResult result = authService.refresh("refresh-token");

            assertThat(result.jwtDto().accessToken()).isEqualTo("new-access-token");
            assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

            verify(inMemoryAuthTokenStore).revoke(null, "refresh-token");
            verify(inMemoryAuthTokenStore).register(user.getId(), "new-access-token", "new-refresh-token");
        }

        @Test
        @DisplayName("리프레시 토큰이 없으면 실패한다")
        void refreshFailWhenTokenMissing() {
            assertThatThrownBy(() -> authService.refresh(null))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("저장된 리프레시 토큰이 아니면 실패한다")
        void refreshFailWhenTokenInvalid() {
            when(inMemoryAuthTokenStore.findUserIdByRefreshToken("refresh-token")).thenReturn(null);

            assertThatThrownBy(() -> authService.refresh("refresh-token"))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Nested
    class ResetPasswordTest {

        @Test
        @DisplayName("비밀번호 초기화에 성공한다")
        void resetPassword() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "old-password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(temporaryPasswordGenerator.generate()).thenReturn("temporary1234");

            authService.resetPassword(new ResetPasswordRequest("test3@test.com"));

            assertThat(user.getPassword()).isEqualTo("temporary1234");
            verify(passwordResetMailSender, times(1))
                    .send("test3@test.com", "temporary1234");
        }

        @Test
        @DisplayName("이메일이 비어있으면 비밀번호 초기화에 실패한다")
        void resetPasswordFailWhenEmailInvalid() {
            assertThatThrownBy(() -> authService.resetPassword(new ResetPasswordRequest("")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_RESET_PASSWORD_REQUEST);
        }

        @Test
        @DisplayName("사용자가 없으면 비밀번호 초기화에 실패한다")
        void resetPasswordFailWhenUserNotFound() {
            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.resetPassword(new ResetPasswordRequest("test3@test.com")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.RESET_PASSWORD_USER_NOT_FOUND);
        }
    }
}