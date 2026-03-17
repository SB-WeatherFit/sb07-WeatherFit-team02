package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.repository.UserSearchCondition;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    class CreateTest {

        @Test
        @DisplayName("회원가입에 성공한다")
        void create() {
            UserCreateRequest request = new UserCreateRequest("tester", "user@test.com", "password");
            User savedUser = User.create("user@test.com", "tester", UserRole.USER, "password");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserDto result = userService.create(request);

            assertThat(result.email()).isEqualTo("user@test.com");
            assertThat(result.name()).isEqualTo("tester");
            assertThat(result.role()).isEqualTo(UserRole.USER);

            verify(profileRepository, times(1)).save(any(Profile.class));
        }

        @Test
        @DisplayName("중복 이메일이면 예외가 발생한다")
        void createFailWhenDuplicatedEmail() {
            UserCreateRequest request = new UserCreateRequest("tester", "user@test.com", "password");

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(WeatherFitException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.DUPLICATED_USER_EMAIL);
        }
    }

    @Nested
    class GetUsersTest {

        @Test
        @DisplayName("사용자 목록 조회에 성공한다")
        void getUsers() {
            User firstUser = User.create("a@test.com", "tester-a", UserRole.USER, "password");
            User secondUser = User.create("b@test.com", "tester-b", UserRole.ADMIN, "password");

            when(userRepository.searchUsers(any(UserSearchCondition.class)))
                    .thenReturn(List.of(firstUser, secondUser));
            when(userRepository.countUsers(any(UserSearchCondition.class)))
                    .thenReturn(2L);

            UserDtoCursorResponse result = userService.getUsers(
                    null,
                    null,
                    20,
                    "createdAt",
                    "DESCENDING",
                    null,
                    null,
                    null
            );

            assertThat(result.data()).hasSize(2);
            assertThat(result.totalCount()).isEqualTo(2L);
            assertThat(result.hasNext()).isFalse();
            assertThat(result.sortBy()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESCENDING");

            ArgumentCaptor<UserSearchCondition> captor = ArgumentCaptor.forClass(UserSearchCondition.class);
            verify(userRepository).searchUsers(captor.capture());

            UserSearchCondition condition = captor.getValue();
            assertThat(condition.limit()).isEqualTo(20);
            assertThat(condition.sortBy()).isEqualTo("createdAt");
            assertThat(condition.sortDirection()).isEqualTo("DESCENDING");
        }
    }

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