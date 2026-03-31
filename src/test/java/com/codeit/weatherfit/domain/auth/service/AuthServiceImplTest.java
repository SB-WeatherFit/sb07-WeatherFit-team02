package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.entity.TemporaryPassword;
import com.codeit.weatherfit.domain.auth.repository.TemporaryPasswordRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
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

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TemporaryPasswordRepository temporaryPasswordRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Nested
    class SignInTest {

        @Test
        @DisplayName("로그인에 성공한다")
        void signIn() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "encoded-password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
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
        @DisplayName("임시 비밀번호로 로그인에 성공한다")
        void signInWithTemporaryPassword() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "encoded-password");
            TemporaryPassword temporaryPassword = TemporaryPassword.create(
                    user,
                    "encoded-temporary-password",
                    Instant.now().plusSeconds(180)
            );

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("temporary-password", "encoded-password")).thenReturn(false);
            when(temporaryPasswordRepository.findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId()))
                    .thenReturn(Optional.of(temporaryPassword));
            when(passwordEncoder.matches("temporary-password", "encoded-temporary-password")).thenReturn(true);
            when(jwtTokenProvider.generateAccessToken(user)).thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken(user)).thenReturn("refresh-token");

            AuthTokenResult result = authService.signIn(new SignInRequest("test3@test.com", "temporary-password"));

            assertThat(result.jwtDto().userDto().email()).isEqualTo("test3@test.com");
            assertThat(result.jwtDto().accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("만료된 임시 비밀번호면 로그인에 실패한다")
        void signInFailWhenTemporaryPasswordExpired() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "encoded-password");
            TemporaryPassword temporaryPassword = TemporaryPassword.create(
                    user,
                    "encoded-temporary-password",
                    Instant.now().minusSeconds(1)
            );

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("temporary-password", "encoded-password")).thenReturn(false);
            when(temporaryPasswordRepository.findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId()))
                    .thenReturn(Optional.of(temporaryPassword));

            assertThatThrownBy(() -> authService.signIn(new SignInRequest("test3@test.com", "temporary-password")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.SIGN_IN_FAILED);
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
            User user = User.create("test3@test.com", "test3", UserRole.USER, "encoded-password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);
            when(temporaryPasswordRepository.findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId()))
                    .thenReturn(Optional.empty());

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
            User user = User.create("test3@test.com", "test3", UserRole.USER, "encoded-password");

            when(jwtTokenProvider.isValidRefreshToken("refresh-token")).thenReturn(true);
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
            when(jwtTokenProvider.isValidRefreshToken("refresh-token")).thenReturn(false);

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
            User user = User.create("test3@test.com", "test3", UserRole.USER, "old-encoded-password");

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(temporaryPasswordRepository.findAllByUserIdAndUsedFalse(user.getId())).thenReturn(List.of());
            when(temporaryPasswordGenerator.generate()).thenReturn("temporary1234");
            when(passwordEncoder.encode("temporary1234")).thenReturn("encoded-temporary1234");

            authService.resetPassword(new ResetPasswordRequest("test3@test.com"));

            ArgumentCaptor<TemporaryPassword> captor = ArgumentCaptor.forClass(TemporaryPassword.class);
            verify(temporaryPasswordRepository).save(captor.capture());

            TemporaryPassword savedTemporaryPassword = captor.getValue();
            assertThat(savedTemporaryPassword.getUser()).isEqualTo(user);
            assertThat(savedTemporaryPassword.getEncodedPassword()).isEqualTo("encoded-temporary1234");
            assertThat(savedTemporaryPassword.isUsed()).isFalse();

            verify(passwordResetMailSender, times(1))
                    .send("test3@test.com", "temporary1234");
        }

        @Test
        @DisplayName("기존 활성 임시 비밀번호가 있으면 사용 처리 후 새로 저장한다")
        void resetPasswordMarksPreviousTemporaryPasswordUsed() {
            User user = User.create("test3@test.com", "test3", UserRole.USER, "old-encoded-password");
            TemporaryPassword previousTemporaryPassword = TemporaryPassword.create(
                    user,
                    "encoded-old-temp",
                    Instant.now().plusSeconds(180)
            );

            when(userRepository.findByEmail("test3@test.com")).thenReturn(Optional.of(user));
            when(temporaryPasswordRepository.findAllByUserIdAndUsedFalse(user.getId()))
                    .thenReturn(List.of(previousTemporaryPassword));
            when(temporaryPasswordGenerator.generate()).thenReturn("temporary1234");
            when(passwordEncoder.encode("temporary1234")).thenReturn("encoded-temporary1234");

            authService.resetPassword(new ResetPasswordRequest("test3@test.com"));

            assertThat(previousTemporaryPassword.isUsed()).isTrue();
            verify(temporaryPasswordRepository).save(any(TemporaryPassword.class));
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