package com.codeit.weatherfit.domain.profile.controller;

import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;
import com.codeit.weatherfit.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileDto get(@PathVariable UUID userId) {
        return profileService.get(userId);
    }

    @PatchMapping
    public ProfileDto update(@PathVariable UUID userId, @Valid @RequestBody ProfileUpdateRequest request) {
        return profileService.update(userId, request);
    }
}