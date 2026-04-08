package com.codeit.weatherfit.domain.auth.service;

import com.codeit.weatherfit.domain.auth.entity.SocialAccount;
import com.codeit.weatherfit.domain.auth.entity.SocialProvider;
import com.codeit.weatherfit.domain.auth.repository.SocialAccountRepository;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2SocialLoginServiceImpl implements OAuth2SocialLoginService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User loadOrCreateUser(SocialProvider provider, OAuth2User oAuth2User) {
        String providerUserId = extractProviderUserId(provider, oAuth2User);
        String email = extractEmail(provider, oAuth2User);
        String name = extractName(provider, oAuth2User);

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElse(null);

        if (socialAccount != null) {
            if (!email.equals(socialAccount.getProviderEmail())) {
                socialAccount.updateProviderEmail(email);
            }

            UUID userId = socialAccount.getUser().getId();

            return userRepository.findById(userId)
                    .orElseThrow(() -> new WeatherFitException(ErrorCode.USER_NOT_FOUND));
        }

        if (userRepository.existsByEmail(email)) {
            throw new WeatherFitException(ErrorCode.SOCIAL_ACCOUNT_EMAIL_ALREADY_EXISTS);
        }

        User savedUser = userRepository.save(User.create(
                email,
                name,
                UserRole.USER,
                passwordEncoder.encode(UUID.randomUUID().toString())
        ));

        Profile profile = Profile.create(savedUser, null, null, null, null, null);
        profileRepository.save(profile);

        SocialAccount newSocialAccount = SocialAccount.create(
                savedUser,
                provider,
                providerUserId,
                email
        );
        socialAccountRepository.save(newSocialAccount);

        return savedUser;
    }

    private String extractProviderUserId(SocialProvider provider, OAuth2User oAuth2User) {
        if (provider == SocialProvider.GOOGLE) {
            Object value = oAuth2User.getAttributes().get("sub");
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
        }

        if (provider == SocialProvider.KAKAO) {
            Object value = oAuth2User.getAttributes().get("id");
            if (value instanceof Number numberValue) {
                return String.valueOf(numberValue.longValue());
            }
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
        }

        throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    private String extractEmail(SocialProvider provider, OAuth2User oAuth2User) {
        if (provider == SocialProvider.GOOGLE) {
            Object value = oAuth2User.getAttributes().get("email");
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
        }

        if (provider == SocialProvider.KAKAO) {
            Map<String, Object> kakaoAccount = getMap(oAuth2User.getAttributes(), "kakao_account");
            Object email = kakaoAccount.get("email");

            if (email instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
        }

        throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    private String extractName(SocialProvider provider, OAuth2User oAuth2User) {
        if (provider == SocialProvider.GOOGLE) {
            Object value = oAuth2User.getAttributes().get("name");
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
            return extractEmail(provider, oAuth2User);
        }

        if (provider == SocialProvider.KAKAO) {
            Map<String, Object> properties = getMap(oAuth2User.getAttributes(), "properties");
            Object nickname = properties.get("nickname");

            if (nickname instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }

            Map<String, Object> kakaoAccount = getMap(oAuth2User.getAttributes(), "kakao_account");
            Map<String, Object> profile = getMap(kakaoAccount, "profile");
            Object profileNickname = profile.get("nickname");

            if (profileNickname instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }

            return extractEmail(provider, oAuth2User);
        }

        throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);

        if (value instanceof Map<?, ?> mapValue) {
            return (Map<String, Object>) mapValue;
        }

        return Map.of();
    }
}