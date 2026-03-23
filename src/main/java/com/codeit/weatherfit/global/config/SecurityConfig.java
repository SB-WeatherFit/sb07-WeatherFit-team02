package com.codeit.weatherfit.global.config;

import com.codeit.weatherfit.domain.auth.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider
    ) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilterProvider.getIfAvailable();

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/h2-console/**",
                                "/api/auth/sign-in",
                                "/api/auth/sign-out",
                                "/api/auth/refresh",
                                "/api/auth/reset-password",
                                "/api/auth/csrf-token"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/h2-console/**",
                                "/api/users/**",
                                "/api/auth/**"
                        )
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .httpBasic(Customizer.withDefaults());

        if (jwtAuthenticationFilter != null) {
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        DefaultSecurityFilterChain chain = http.build();
        return chain;
    }
}