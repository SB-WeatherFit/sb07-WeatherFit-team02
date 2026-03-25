package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryAuthTokenStore inMemoryAuthTokenStore;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = extractAccessToken(authorizationHeader);

        if (accessToken != null
                && jwtTokenProvider.isValidAccessToken(accessToken)
                && !inMemoryAuthTokenStore.isRevokedAccessToken(accessToken)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UUID userId = jwtTokenProvider.getUserId(accessToken);

            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);

                if (user != null && !user.isLocked()) {
                    WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring(7);
    }
}