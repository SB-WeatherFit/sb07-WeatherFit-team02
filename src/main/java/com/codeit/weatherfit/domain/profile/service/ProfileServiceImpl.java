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
import com.codeit.weatherfit.global.s3.S3Service;
import com.codeit.weatherfit.global.s3.event.S3ProfilePutEvent;
import com.codeit.weatherfit.global.s3.exception.S3UploadException;
import com.codeit.weatherfit.global.s3.util.S3KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileLocationResolver profileLocationResolver;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto get(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));

        return ProfileDto.from(profile, getProfileImageUrl(profile));
    }

    @Override
    public ProfileDto update(UUID userId, ProfileUpdateRequest request, MultipartFile image) {
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

        if (image != null) {
            try {
                String key = S3KeyGenerator.generateKey(image.getOriginalFilename());
                eventPublisher.publishEvent(new S3ProfilePutEvent(
                        userId,
                        key,
                        image.getContentType(),
                        image.getBytes()));
                profile.updateProfileImageKey(key);
            } catch (IOException e) {
                throw new S3UploadException(image.getOriginalFilename());
            }
        }

        profile.updateGender(request.gender());
        profile.updateBirthDate(request.birthDate());
        profile.updateLocation(location);
        profile.updateTemperatureSensitivity(request.temperatureSensitivity());

        return ProfileDto.from(profile, getProfileImageUrl(profile));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearImageKey(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new WeatherFitException(ErrorCode.PROFILE_NOT_FOUND));
        profile.updateProfileImageKey(null);
    }

    private String getProfileImageUrl(Profile profile) {
        String profileImageKey = profile.getProfileImageKey();
        if (profileImageKey == null || profileImageKey.isBlank()) {
            return null;
        }
        return s3Service.getUrl(profileImageKey);
    }
}