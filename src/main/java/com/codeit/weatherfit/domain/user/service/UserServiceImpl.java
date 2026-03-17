package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new WeatherFitException(ErrorCode.DUPLICATED_USER_EMAIL);
        }

        User user = User.create(
                request.email(),
                request.name(),
                UserRole.USER,
                request.password()
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
        String normalizedEmailLike = emailLike == null ? "" : emailLike;
        UserRole role = roleEqual == null || roleEqual.isBlank() ? null : UserRole.valueOf(roleEqual);
        PageRequest pageable = PageRequest.of(0, limit + 1);

        List<User> users;
        if (cursor == null || cursor.isBlank()) {
            users = getFirstPage(normalizedEmailLike, role, locked, pageable);
        } else {
            Instant cursorInstant = Instant.parse(cursor);
            users = getNextPage(normalizedEmailLike, role, locked, cursorInstant, pageable);
        }

        long totalCount = countUsers(normalizedEmailLike, role, locked);
        boolean hasNext = users.size() > limit;
        List<User> pageContent = hasNext ? users.subList(0, limit) : users;

        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            User lastUser = pageContent.get(pageContent.size() - 1);
            nextCursor = lastUser.getCreatedAt().toString();
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
                sortBy,
                sortDirection
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

    private List<User> getFirstPage(
            String emailLike,
            UserRole role,
            Boolean locked,
            PageRequest pageable
    ) {
        if (role != null && locked != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndRoleAndLockedOrderByCreatedAtDescIdDesc(
                    emailLike, role, locked, pageable
            );
        }

        if (role != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndRoleOrderByCreatedAtDescIdDesc(
                    emailLike, role, pageable
            );
        }

        if (locked != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndLockedOrderByCreatedAtDescIdDesc(
                    emailLike, locked, pageable
            );
        }

        return userRepository.findByEmailContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
                emailLike, pageable
        );
    }

    private List<User> getNextPage(
            String emailLike,
            UserRole role,
            Boolean locked,
            Instant cursor,
            PageRequest pageable
    ) {
        if (role != null && locked != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndRoleAndLockedAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
                    emailLike, role, locked, cursor, pageable
            );
        }

        if (role != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndRoleAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
                    emailLike, role, cursor, pageable
            );
        }

        if (locked != null) {
            return userRepository.findByEmailContainingIgnoreCaseAndLockedAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
                    emailLike, locked, cursor, pageable
            );
        }

        return userRepository.findByEmailContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
                emailLike, cursor, pageable
        );
    }

    private long countUsers(String emailLike, UserRole role, Boolean locked) {
        if (role != null && locked != null) {
            return userRepository.countByEmailContainingIgnoreCaseAndRoleAndLocked(emailLike, role, locked);
        }

        if (role != null) {
            return userRepository.countByEmailContainingIgnoreCaseAndRole(emailLike, role);
        }

        if (locked != null) {
            return userRepository.countByEmailContainingIgnoreCaseAndLocked(emailLike, locked);
        }

        return userRepository.countByEmailContainingIgnoreCase(emailLike);
    }
}