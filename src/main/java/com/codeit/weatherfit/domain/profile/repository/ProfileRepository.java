package com.codeit.weatherfit.domain.profile.repository;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID userId);

    @Query("select p from Profile p" +
            " join fetch p.user u" +
            " where u.id = :userId")
    Optional<Profile> findWithUser(@Param("userId") UUID userId);

    @Query("select p from Profile p" +
            " join fetch p.user u" +
            " where u.id in :ids")
    List<Profile> findByUserIds(@Param("ids") List<UUID> ids);

    @Query("select p.profileImageKey from Profile p" +
            " where p.profileImageKey is not null")
    Set<String> findAllImageKeys();
}