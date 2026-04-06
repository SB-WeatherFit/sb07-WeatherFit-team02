package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.auth.repository.TemporaryPasswordRepository;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.dto.response.UserSummary;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.user.repository.UserSearchCondition;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import com.codeit.weatherfit.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final TemporaryPasswordRepository temporaryPasswordRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new WeatherFitException(ErrorCode.DUPLICATED_USER_EMAIL);
        }

        User user = User.create(
                request.email(),
                request.name(),
                UserRole.USER,
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);
        Profile profile = Profile.create(savedUser, null, null, null, null, null);
        profileRepository.save(profile);

        return UserDto.from(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtoCursorResponse getUsers(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            String sortDirection,
            String emailLike,
            String roleEqual,
            Boolean locked
    ) {
        UserRole parsedRoleEqual = parseRole(roleEqual);

        UserSearchCondition condition = new UserSearchCondition(
                cursor,
                idAfter,
                limit,
                sortBy,
                sortDirection,
                emailLike,
                parsedRoleEqual,
                locked
        );

        List<User> users = userRepository.searchUsers(condition);
        long totalCount = userRepository.countUsers(condition);

        boolean hasNext = users.size() > limit;
        List<User> pageContent = hasNext ? users.subList(0, limit) : users;

        String normalizedSortBy = normalizeSortBy(sortBy);
        String normalizedSortDirection = normalizeSortDirection(sortDirection);

        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            User lastUser = pageContent.get(pageContent.size() - 1);
            nextCursor = extractNextCursor(lastUser, normalizedSortBy);
            nextIdAfter = lastUser.getId();
        }

        List<UserDto> data = pageContent.stream()
                .map(UserDto::from)
                .toList();

        return UserDtoCursorResponse.of(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                normalizedSortBy,
                normalizedSortDirection
        );
    }

    @Override
    public UserDto updateRole(UUID userId, UserRoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(request.role());

        return UserDto.from(user);
    }

    @Override
    public UserDto updateLock(UUID userId, UserLockUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        user.updateLockState(request.locked());

        return UserDto.from(user);
    }

    @Override
    public void updatePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(request.password()));
        temporaryPasswordRepository.deleteAllByUserIdAndUsedFalse(userId);
    }

    @Override
    public UserSummary getUserSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));

        return UserSummary.from(user, getProfileImageUrl(profile));
    }

    @Override
    public UserSummary getUserSummary(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));

        return UserSummary.from(user, getProfileImageUrl(profile));
    }

    private String getProfileImageUrl(Profile profile) {
        String profileImageKey = profile.getProfileImageKey();
        if (profileImageKey == null || profileImageKey.isBlank()) {
            return null;
        }
        return s3Service.getUrl(profileImageKey);
    }

    private UserRole parseRole(String roleEqual) {
        if (roleEqual == null || roleEqual.isBlank()) {
            return null;
        }

        return UserRole.valueOf(roleEqual);
    }

    private String normalizeSortBy(String sortBy) {
        if ("email".equals(sortBy)) {
            return "email";
        }

        return "createdAt";
    }

    private String normalizeSortDirection(String sortDirection) {
        if ("ASCENDING".equals(sortDirection)) {
            return "ASCENDING";
        }

        return "DESCENDING";
    }

    private String extractNextCursor(User user, String sortBy) {
        if ("email".equals(sortBy)) {
            return user.getEmail();
        }

        return user.getCreatedAt().toString();
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void initializeAdmin() {
        if(userRepository.existsUserByRole(UserRole.ADMIN)) {return;}

        User user = User.create(
                "admin@admin.com",
                "admin",
                UserRole.ADMIN,
                passwordEncoder.encode("admin1234")
        );

        User savedUser = userRepository.save(user);
        Profile profile = Profile.create(savedUser, null, null,
                Location.create(
                        37.2911,
                        127.0089,
                        127,
                        37,
                        List.of("경기도", "로날도", "수원")

                )

                , null, null);
        profileRepository.save(profile);
    }
}