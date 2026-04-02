package com.codeit.weatherfit.domain.auth.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@Table(
        name = "social_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_social_accounts_provider_user_id", columnNames = {"provider", "provider_user_id"}),
                @UniqueConstraint(name = "uk_social_accounts_user_provider", columnNames = {"user_id", "provider"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "provider_email", nullable = false)
    private String providerEmail;

    private SocialAccount(
            User user,
            SocialProvider provider,
            String providerUserId,
            String providerEmail
    ) {
        validateUser(user);
        validateProvider(provider);
        validateProviderUserId(providerUserId);
        validateProviderEmail(providerEmail);

        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.providerEmail = providerEmail;
    }

    public static SocialAccount create(
            User user,
            SocialProvider provider,
            String providerUserId,
            String providerEmail
    ) {
        return new SocialAccount(user, provider, providerUserId, providerEmail);
    }

    public void updateProviderEmail(String providerEmail) {
        validateProviderEmail(providerEmail);
        this.providerEmail = providerEmail;
    }

    private static void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new WeatherFitException(ErrorCode.INVALID_SOCIAL_ACCOUNT_USER);
        }
    }

    private static void validateProvider(SocialProvider provider) {
        if (Objects.isNull(provider)) {
            throw new WeatherFitException(ErrorCode.INVALID_SOCIAL_PROVIDER);
        }
    }

    private static void validateProviderUserId(String providerUserId) {
        if (Objects.isNull(providerUserId) || providerUserId.isBlank()) {
            throw new WeatherFitException(ErrorCode.INVALID_SOCIAL_PROVIDER_USER_ID);
        }
    }

    private static void validateProviderEmail(String providerEmail) {
        if (Objects.isNull(providerEmail) || providerEmail.isBlank()) {
            throw new WeatherFitException(ErrorCode.INVALID_SOCIAL_PROVIDER_EMAIL);
        }
    }
}