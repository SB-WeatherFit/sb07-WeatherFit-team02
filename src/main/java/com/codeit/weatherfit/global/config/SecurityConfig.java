package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.auth.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.codeit.weatherfit.domain.auth.security.JwtAuthenticationFilter;
import com.codeit.weatherfit.domain.auth.security.OAuth2AuthenticationFailureHandler;
import com.codeit.weatherfit.domain.auth.security.OAuth2AuthenticationSuccessHandler;
import com.codeit.weatherfit.global.security.CustomAccessDeniedHandler;
import com.codeit.weatherfit.global.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider
    ) throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookiePath("/");
        csrfTokenRepository.setHeaderName("X-XSRF-TOKEN");

        boolean oauth2Enabled = clientRegistrationRepositoryProvider.getIfAvailable() != null;

        http
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(
                                    "/",
                                    "/index.html",
                                    "/login",
                                    "/error",
                                    "/favicon.ico",
                                    "/assets/**",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
                                    "/*.svg",
                                    "/*.png",
                                    "/*.jpg",
                                    "/*.jpeg",
                                    "/*.webp",
                                    "/actuator/health",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v3/api-docs/**"
                            ).permitAll()
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/ws/**").permitAll()
                            .requestMatchers("/api/auth/sign-in").permitAll()
                            .requestMatchers("/api/auth/sign-out").permitAll()
                            .requestMatchers("/api/auth/refresh").permitAll()
                            .requestMatchers("/api/auth/reset-password").permitAll()
                            .requestMatchers("/api/auth/csrf-token").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/users").permitAll();

                    if (oauth2Enabled) {
                        authorize
                                .requestMatchers("/oauth2/authorization/**").permitAll()
                                .requestMatchers("/login/oauth2/code/**").permitAll();
                    }

                    authorize.anyRequest().authenticated();
                })
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/h2-console/**",
                                "/ws/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        )
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable());

        if (oauth2Enabled) {
            http.oauth2Login(oauth2 -> oauth2
                    .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                    )
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            );
        }

        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilterProvider.getIfAvailable();
        if (jwtAuthenticationFilter != null) {
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Supplier<CsrfToken> csrfToken
        ) {
            xor.handle(request, response, csrfToken);
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            if (StringUtils.hasText(headerValue)) {
                return plain.resolveCsrfTokenValue(request, csrfToken);
            }
            return xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}