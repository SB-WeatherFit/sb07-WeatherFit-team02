package com.codeit.weatherfit.domain.auth.controller;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;
import com.codeit.weatherfit.domain.auth.service.AuthService;
import com.codeit.weatherfit.domain.auth.service.AuthTokenResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(MultipartHttpServletRequest request) {
        SignInRequest signInRequest = new SignInRequest(
                request.getParameter("username"),
                request.getParameter("password")
        );

        AuthTokenResult authTokenResult = authService.signIn(signInRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(authTokenResult.refreshToken()).toString())
                .body(authTokenResult.jwtDto());
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken
    ) {
        authService.signOut(authorizationHeader, refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie().toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken
    ) {
        AuthTokenResult authTokenResult = authService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(authTokenResult.refreshToken()).toString())
                .body(authTokenResult.jwtDto());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> csrfToken(CsrfToken csrfToken) {
        csrfToken.getToken();
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(60L * 60L * 24L * 7L)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}