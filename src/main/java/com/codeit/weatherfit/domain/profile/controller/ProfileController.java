package com.codeit.weatherfit.domain.profile.controller;

import com.codeit.weatherfit.domain.profile.controller.docs.ProfileControllerDocs;
import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;
import com.codeit.weatherfit.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/profiles")
public class ProfileController implements ProfileControllerDocs {

    private final ProfileService profileService;

    @GetMapping
    public ProfileDto get(@PathVariable UUID userId) {
        return profileService.get(userId);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileDto update(@PathVariable UUID userId,
                             @Valid @ParameterObject @ModelAttribute ProfileUpdateRequest request,
                             @RequestPart(value = "image", required = false) MultipartFile image) {
        return profileService.update(userId, request, image);
    }
}