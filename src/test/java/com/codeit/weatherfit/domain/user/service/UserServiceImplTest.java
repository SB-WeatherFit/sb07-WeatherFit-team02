package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    class UpdateRoleTest {

        @Test
        @DisplayName("사용자 권한 수정에 성공한다")
        void updateRole() {
            UUID userId = UUID.randomUUID();
            User user = User.create("user@test.com", "tester", UserRole.USER, "password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserDto result = userService.updateRole(userId, new UserRoleUpdateRequest(UserRole.ADMIN));

            assertThat(result.role()).isEqualTo(UserRole.ADMIN);
        }

        @Test
        @DisplayName("사용자가 없으면 예외가 발생한다")
        void updateRoleFailWhenUserNotFound() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateRole(userId, new UserRoleUpdateRequest(UserRole.ADMIN)))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    class UpdateLockTest {

        @Test
        @DisplayName("사용자 잠금 상태 수정에 성공한다")
        void updateLock() {
            UUID userId = UUID.randomUUID();
            User user = User.create("user@test.com", "tester", UserRole.USER, "password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserDto result = userService.updateLock(userId, new UserLockUpdateRequest(true));

            assertThat(result.locked()).isTrue();
        }

        @Test
        @DisplayName("사용자가 없으면 예외가 발생한다")
        void updateLockFailWhenUserNotFound() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateLock(userId, new UserLockUpdateRequest(true)))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    class UpdatePasswordTest {

        @Test
        @DisplayName("비밀번호 변경에 성공한다")
        void updatePassword() {
            UUID userId = UUID.randomUUID();
            User user = User.create("user@test.com", "tester", UserRole.USER, "password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.updatePassword(userId, new ChangePasswordRequest("new-password"));

            assertThat(user.getPassword()).isEqualTo("new-password");
        }

        @Test
        @DisplayName("비밀번호 변경 시 사용자가 없으면 예외가 발생한다")
        void updatePasswordFailWhenUserNotFound() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updatePassword(userId, new ChangePasswordRequest("new-password")))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }
}