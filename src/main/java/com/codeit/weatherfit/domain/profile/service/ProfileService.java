package com.codeit.weatherfit.domain.profile.service;

import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;

import java.util.UUID;

public interface ProfileService {

    ProfileDto get(UUID userId);

    ProfileDto update(UUID userId, ProfileUpdateRequest request);
}