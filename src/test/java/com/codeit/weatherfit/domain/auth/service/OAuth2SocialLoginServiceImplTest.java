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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SocialLoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OAuth2SocialLoginServiceImpl oAuth2SocialLoginService;

    @Nested
    class GoogleLoginTest {

        @Test
        @DisplayName("Google 최초 로그인 시 user, profile, social account를 생성한다")
        void loadOrCreateUser_google_firstLogin() {
            OAuth2User oAuth2User = googleUser("google-sub-1", "user@test.com", "tester");

            when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub-1"))
                    .thenReturn(Optional.empty());
            when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

            User savedUser = User.create("user@test.com", "tester", UserRole.USER, "encoded-password");

            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(socialAccountRepository.save(any(SocialAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.GOOGLE, oAuth2User);

            assertThat(result.getEmail()).isEqualTo("user@test.com");
            assertThat(result.getName()).isEqualTo("tester");
            assertThat(result.getRole()).isEqualTo(UserRole.USER);

            verify(userRepository).save(any(User.class));
            verify(profileRepository).save(any(Profile.class));

            ArgumentCaptor<SocialAccount> socialAccountCaptor = ArgumentCaptor.forClass(SocialAccount.class);
            verify(socialAccountRepository).save(socialAccountCaptor.capture());

            SocialAccount savedSocialAccount = socialAccountCaptor.getValue();
            assertThat(savedSocialAccount.getProvider()).isEqualTo(SocialProvider.GOOGLE);
            assertThat(savedSocialAccount.getProviderUserId()).isEqualTo("google-sub-1");
            assertThat(savedSocialAccount.getProviderEmail()).isEqualTo("user@test.com");
        }

        @Test
        @DisplayName("Google 재로그인 시 기존 user를 반환한다")
        void loadOrCreateUser_google_relogin() {
            OAuth2User oAuth2User = googleUser("google-sub-1", "user@test.com", "tester");

            User existingUser = User.create("user@test.com", "tester", UserRole.USER, "encoded-password");
            SocialAccount socialAccount = SocialAccount.create(
                    existingUser,
                    SocialProvider.GOOGLE,
                    "google-sub-1",
                    "user@test.com"
            );

            when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub-1"))
                    .thenReturn(Optional.of(socialAccount));
            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

            User result = oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.GOOGLE, oAuth2User);

            assertThat(result).isEqualTo(existingUser);
            verify(userRepository, never()).save(any(User.class));
            verify(profileRepository, never()).save(any(Profile.class));
        }
    }

    @Nested
    class KakaoLoginTest {

        @Test
        @DisplayName("Kakao 최초 로그인 시 user, profile, social account를 생성한다")
        void loadOrCreateUser_kakao_firstLogin() {
            OAuth2User oAuth2User = kakaoUser(12345L, "kakao@test.com", "kakao-user");

            when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.KAKAO, "12345"))
                    .thenReturn(Optional.empty());
            when(userRepository.existsByEmail("kakao@test.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

            User savedUser = User.create("kakao@test.com", "kakao-user", UserRole.USER, "encoded-password");

            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(socialAccountRepository.save(any(SocialAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.KAKAO, oAuth2User);

            assertThat(result.getEmail()).isEqualTo("kakao@test.com");
            assertThat(result.getName()).isEqualTo("kakao-user");

            ArgumentCaptor<SocialAccount> socialAccountCaptor = ArgumentCaptor.forClass(SocialAccount.class);
            verify(socialAccountRepository).save(socialAccountCaptor.capture());

            SocialAccount savedSocialAccount = socialAccountCaptor.getValue();
            assertThat(savedSocialAccount.getProvider()).isEqualTo(SocialProvider.KAKAO);
            assertThat(savedSocialAccount.getProviderUserId()).isEqualTo("12345");
            assertThat(savedSocialAccount.getProviderEmail()).isEqualTo("kakao@test.com");
        }

        @Test
        @DisplayName("Kakao 재로그인 시 기존 user를 반환한다")
        void loadOrCreateUser_kakao_relogin() {
            OAuth2User oAuth2User = kakaoUser(12345L, "kakao@test.com", "kakao-user");

            User existingUser = User.create("kakao@test.com", "kakao-user", UserRole.USER, "encoded-password");
            SocialAccount socialAccount = SocialAccount.create(
                    existingUser,
                    SocialProvider.KAKAO,
                    "12345",
                    "kakao@test.com"
            );

            when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.KAKAO, "12345"))
                    .thenReturn(Optional.of(socialAccount));
            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

            User result = oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.KAKAO, oAuth2User);

            assertThat(result).isEqualTo(existingUser);
            verify(userRepository, never()).save(any(User.class));
            verify(profileRepository, never()).save(any(Profile.class));
        }
    }

    @Test
    @DisplayName("동일 이메일의 일반 계정이 이미 있으면 예외가 발생한다")
    void loadOrCreateUser_fail_whenEmailAlreadyExists() {
        OAuth2User oAuth2User = googleUser("google-sub-1", "user@test.com", "tester");

        when(socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-sub-1"))
                .thenReturn(Optional.empty());
        when(userRepository.existsByEmail("user@test.com")).thenReturn(true);

        assertThatThrownBy(() -> oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.GOOGLE, oAuth2User))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_ACCOUNT_EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Google 이메일 정보가 없으면 예외가 발생한다")
    void loadOrCreateUser_fail_whenGoogleEmailMissing() {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of(
                        "sub", "google-sub-1",
                        "name", "tester"
                ),
                "sub"
        );

        assertThatThrownBy(() -> oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.GOOGLE, oAuth2User))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    @Test
    @DisplayName("Kakao 이메일 정보가 없으면 예외가 발생한다")
    void loadOrCreateUser_fail_whenKakaoEmailMissing() {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of(
                        "id", 12345L,
                        "properties", Map.of("nickname", "kakao-user"),
                        "kakao_account", Map.of()
                ),
                "id"
        );

        assertThatThrownBy(() -> oAuth2SocialLoginService.loadOrCreateUser(SocialProvider.KAKAO, oAuth2User))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    private OAuth2User googleUser(String sub, String email, String name) {
        return new DefaultOAuth2User(
                List.of(),
                Map.of(
                        "sub", sub,
                        "email", email,
                        "name", name
                ),
                "sub"
        );
    }

    private OAuth2User kakaoUser(Long id, String email, String nickname) {
        return new DefaultOAuth2User(
                List.of(),
                Map.of(
                        "id", id,
                        "properties", Map.of(
                                "nickname", nickname
                        ),
                        "kakao_account", Map.of(
                                "email", email,
                                "profile", Map.of(
                                        "nickname", nickname
                                )
                        )
                ),
                "id"
        );
    }
}