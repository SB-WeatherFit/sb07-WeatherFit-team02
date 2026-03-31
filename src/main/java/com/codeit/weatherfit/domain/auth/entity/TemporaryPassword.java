package com.codeit.weatherfit.domain.auth.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Table(name = "temporary_passwords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemporaryPassword extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "encoded_password", nullable = false)
    private String encodedPassword;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    private TemporaryPassword(
            User user,
            String encodedPassword,
            Instant expiresAt,
            boolean used
    ) {
        validateUser(user);
        validateEncodedPassword(encodedPassword);
        validateExpiresAt(expiresAt);

        this.user = user;
        this.encodedPassword = encodedPassword;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public static TemporaryPassword create(
            User user,
            String encodedPassword,
            Instant expiresAt
    ) {
        return new TemporaryPassword(user, encodedPassword, expiresAt, false);
    }

    public void markUsed() {
        this.used = true;
    }

    public boolean isExpired(Instant now) {
        validateNow(now);
        return !expiresAt.isAfter(now);
    }

    public boolean isAvailable(Instant now) {
        validateNow(now);
        return !used && !isExpired(now);
    }

    private static void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new WeatherFitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static void validateEncodedPassword(String encodedPassword) {
        if (Objects.isNull(encodedPassword) || encodedPassword.isBlank()) {
            throw new WeatherFitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static void validateExpiresAt(Instant expiresAt) {
        if (Objects.isNull(expiresAt)) {
            throw new WeatherFitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static void validateNow(Instant now) {
        if (Objects.isNull(now)) {
            throw new WeatherFitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}