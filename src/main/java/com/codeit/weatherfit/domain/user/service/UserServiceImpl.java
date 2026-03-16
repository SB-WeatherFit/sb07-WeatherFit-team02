package com.codeit.weatherfit.domain.user.service;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}