package com.codeit.weatherfit.domain.user.controller;

import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }
}