package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.auth.service.OAuth2SocialLoginService;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private OAuth2SocialLoginService oAuth2SocialLoginService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private InMemoryAuthTokenStore inMemoryAuthTokenStore;

    @Mock
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "successRedirectUri", "http://localhost:8080/index.html");
    }

    @Test
    @DisplayName("Google 로그인 성공 시 access token, refresh cookie, redirect를 처리한다")
    void onAuthenticationSuccess_google() throws Exception {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "google-sub-1", "email", "user@test.com", "name", "tester"),
                "sub"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                List.of(),
                "google"
        );

        User user = User.create("user@test.com", "tester", UserRole.USER, "encoded-password");
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(oAuth2SocialLoginService.loadOrCreateUser(any(), eq(oAuth2User))).thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[0]);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(inMemoryAuthTokenStore).register(user.getId(), accessToken, refreshToken);
        verify(response).addHeader(eq("Set-Cookie"), contains("REFRESH_TOKEN="));
        verify(response).sendRedirect(contains("socialLogin=true"));
        verify(response).sendRedirect(contains("accessToken=access-token"));
        verify(httpCookieOAuth2AuthorizationRequestRepository)
                .removeAuthorizationRequestCookies(request, response);
    }

    @Test
    @DisplayName("Kakao 로그인 성공 시 access token, refresh cookie, redirect를 처리한다")
    void onAuthenticationSuccess_kakao() throws Exception {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of(
                        "id", 12345L,
                        "properties", Map.of("nickname", "kakao-user"),
                        "kakao_account", Map.of("email", "kakao@test.com")
                ),
                "id"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                List.of(),
                "kakao"
        );

        User user = User.create("kakao@test.com", "kakao-user", UserRole.USER, "encoded-password");
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(oAuth2SocialLoginService.loadOrCreateUser(any(), eq(oAuth2User))).thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[0]);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(inMemoryAuthTokenStore).register(user.getId(), accessToken, refreshToken);
        verify(response).addHeader(eq("Set-Cookie"), contains("REFRESH_TOKEN="));
        verify(response).sendRedirect(contains("socialLogin=true"));
        verify(response).sendRedirect(contains("accessToken=access-token"));
        verify(httpCookieOAuth2AuthorizationRequestRepository)
                .removeAuthorizationRequestCookies(request, response);
    }

    @Test
    @DisplayName("잠긴 사용자는 예외가 발생한다")
    void onAuthenticationSuccess_fail_whenUserLocked() {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "google-sub-1", "email", "user@test.com", "name", "tester"),
                "sub"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                List.of(),
                "google"
        );

        User lockedUser = User.create("user@test.com", "tester", UserRole.USER, "encoded-password");
        lockedUser.updateLockState(true);

        when(oAuth2SocialLoginService.loadOrCreateUser(any(), eq(oAuth2User))).thenReturn(lockedUser);

        assertThatThrownBy(() -> successHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }

    @Test
    @DisplayName("지원하지 않는 registrationId이면 예외가 발생한다")
    void onAuthenticationSuccess_fail_whenUnsupportedProvider() {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(),
                Map.of("id", "test"),
                "id"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                List.of(),
                "github"
        );

        assertThatThrownBy(() -> successHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }

    @Test
    @DisplayName("OAuth2AuthenticationToken이 아니면 예외가 발생한다")
    void onAuthenticationSuccess_fail_whenNotOAuth2AuthenticationToken() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("principal", "credentials");

        assertThatThrownBy(() -> successHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(WeatherFitException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_SIGN_IN_FAILED);
    }
}