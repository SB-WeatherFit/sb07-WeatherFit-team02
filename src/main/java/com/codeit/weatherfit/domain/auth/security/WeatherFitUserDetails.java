package com.codeit.weatherfit.domain.auth.security;

import com.codeit.weatherfit.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class WeatherFitUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String password;
    private final boolean locked;
    private final Collection<? extends GrantedAuthority> authorities;

    private WeatherFitUserDetails(
            UUID userId,
            String email,
            String password,
            boolean locked,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.locked = locked;
        this.authorities = authorities;
    }

    public static WeatherFitUserDetails from(User user) {
        return new WeatherFitUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }
}