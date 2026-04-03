package com.codeit.weatherfit.domain.auth.repository;

import com.codeit.weatherfit.domain.auth.entity.SocialAccount;
import com.codeit.weatherfit.domain.auth.entity.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, UUID> {

    @Query("""
            select socialAccount
            from SocialAccount socialAccount
            join fetch socialAccount.user user
            where socialAccount.provider = :provider
              and socialAccount.providerUserId = :providerUserId
            """)
    Optional<SocialAccount> findByProviderAndProviderUserId(
            @Param("provider") SocialProvider provider,
            @Param("providerUserId") String providerUserId
    );
}