package com.codeit.weatherfit.domain.profile.service;

import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto get(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("profile not found"));

        return ProfileDto.from(profile);
    }

    @Override
    public ProfileDto update(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("profile not found"));

        user.changeName(request.name());

        Location location = Location.create(
                request.location().latitude(),
                request.location().longitude(),
                request.location().x(),
                request.location().y(),
                request.location().locationNames()
        );

        profile.update(
                request.gender(),
                request.birthDate(),
                location,
                request.temperatureSensitivity(),
                profile.getProfileImageUrl()
        );

        return ProfileDto.from(profile);
    }
}