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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${weatherfit.auth.social.success-redirect-uri:http://localhost:8080/index.html}")
    private String successRedirectUri;

    private final OAuth2SocialLoginService oAuth2SocialLoginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryAuthTokenStore inMemoryAuthTokenStore;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        SocialProvider socialProvider = parseProvider(registrationId);

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User oAuth2User)) {
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        User user = oAuth2SocialLoginService.loadOrCreateUser(socialProvider, oAuth2User);

        if (user.isLocked()) {
            throw new WeatherFitException(ErrorCode.SOCIAL_SIGN_IN_FAILED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        inMemoryAuthTokenStore.register(user.getId(), accessToken, refreshToken);

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        URI redirectUri = UriComponentsBuilder
                .fromUriString(successRedirectUri)
                .queryParam("socialLogin", true)
                .queryParam("accessToken", accessToken)
                .build(true)
                .toUri();

        clearOAuth2StateCookies(request, response);

        response.sendRedirect(redirectUri.toString());
    }

    private SocialProvider parseProvider(String registrationId) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return SocialProvider.GOOGLE;
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