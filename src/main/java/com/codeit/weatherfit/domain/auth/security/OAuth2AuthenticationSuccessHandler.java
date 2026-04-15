package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.auth.entity.SocialProvider;
import com.codeit.weatherfit.domain.auth.service.OAuth2SocialLoginService;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${weatherfit.auth.social.success-redirect-uri:https://www.weatherfit.cloud/}")
    private String successRedirectUri;

    private final OAuth2SocialLoginService oAuth2SocialLoginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryAuthTokenStore inMemoryAuthTokenStore;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        log.info("[OAuth2 success handler start] uri={}", request.getRequestURI());

        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
            log.error("[OAuth2 success handler] authentication type invalid: {}", authentication.getClass().getName());
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        log.info("[OAuth2 success handler] registrationId={}", registrationId);

        SocialProvider socialProvider = parseProvider(registrationId);

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User oAuth2User)) {
            log.error("[OAuth2 success handler] principal type invalid: {}", principal.getClass().getName());
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        log.info("[OAuth2 success handler] attributes={}", oAuth2User.getAttributes());

        User user = oAuth2SocialLoginService.loadOrCreateUser(socialProvider, oAuth2User);
        log.info("[OAuth2 success handler] user loaded. userId={}, email={}", user.getId(), user.getEmail());

        if (user.isLocked()) {
            log.error("[OAuth2 success handler] user locked. userId={}", user.getId());
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        log.info("[OAuth2 success handler] tokens generated. userId={}", user.getId());

        inMemoryAuthTokenStore.register(user.getId(), accessToken, refreshToken);
        log.info("[OAuth2 success handler] tokens registered. userId={}", user.getId());

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        clearOAuth2StateCookies(request, response);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        log.info("[OAuth2 success handler] redirectUri={}", successRedirectUri);
        response.sendRedirect(successRedirectUri);
    }

    private SocialProvider parseProvider(String registrationId) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return SocialProvider.GOOGLE;
        }

        if ("kakao".equalsIgnoreCase(registrationId)) {
            return SocialProvider.KAKAO;
        }

        throw new WeatherFitException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(60L * 60L * 24L * 7L)
                .sameSite("Lax")
                .build();
    }

    private void clearOAuth2StateCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().startsWith("JSESSIONID")) {
                Cookie removeCookie = new Cookie(cookie.getName(), "");
                removeCookie.setPath("/");
                removeCookie.setMaxAge(0);
                response.addCookie(removeCookie);
            }
        }
    }
}