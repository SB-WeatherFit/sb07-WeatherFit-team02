package com.codeit.weatherfit.domain.profile.service;

import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;
import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.location.ProfileLocationResolver;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
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
    private final ProfileLocationResolver profileLocationResolver;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto get(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));

        return ProfileDto.from(profile);
    }

    @Override
    public ProfileDto update(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));

        user.updateName(request.name());

        Location location = request.location() == null
                ? null
                : profileLocationResolver.resolve(
                request.location().latitude(),
                request.location().longitude()
        );

        profile.updateGender(request.gender());
        profile.updateBirthDate(request.birthDate());
        profile.updateLocation(location);
        profile.updateTemperatureSensitivity(request.temperatureSensitivity());

        return ProfileDto.from(profile);
    }
}