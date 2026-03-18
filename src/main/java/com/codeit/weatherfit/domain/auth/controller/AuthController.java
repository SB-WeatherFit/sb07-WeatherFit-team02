package com.codeit.weatherfit.domain.auth.controller;

import com.codeit.weatherfit.domain.auth.dto.request.SignInRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;
import com.codeit.weatherfit.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public JwtDto signIn(MultipartHttpServletRequest request) {
        SignInRequest signInRequest = new SignInRequest(
                request.getParameter("username"),
                request.getParameter("password")
        );

        return authService.signIn(signInRequest);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        authService.signOut(authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> csrfToken(CsrfToken csrfToken) {
        return ResponseEntity.noContent().build();
    }
}