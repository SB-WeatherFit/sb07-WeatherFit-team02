package com.codeit.weatherfit.domain.user.controller;

import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public UserDtoCursorResponse getUsers(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam int limit,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) String emailLike,
            @RequestParam(required = false) String roleEqual,
            @RequestParam(required = false) Boolean locked
    ) {
        return userService.getUsers(
                cursor,
                idAfter,
                limit,
                sortBy,
                sortDirection,
                emailLike,
                roleEqual,
                locked
        );
    }

    @PatchMapping("/{userId}/role")
    public UserDto updateRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UserRoleUpdateRequest request
    ) {
        return userService.updateRole(userId, request);
    }

    @PatchMapping("/{userId}/lock")
    public UserDto updateLock(
            @PathVariable UUID userId,
            @Valid @RequestBody UserLockUpdateRequest request
    ) {
        return userService.updateLock(userId, request);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.updatePassword(userId, request);
        return ResponseEntity.noContent().build();
    }
}