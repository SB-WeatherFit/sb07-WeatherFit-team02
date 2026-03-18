package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowCustomRepository {
    boolean existsByFolloweeIdAndFollowerId(UUID followeeId, UUID followerId);

    @Query("select count(f.id) from Follow  f" +
            " where f.followee.id = :userId")
    long getFollowerCount(
            @Param("userId") UUID userId);

    @Query("select count(f.id) from Follow  f" +
            " where f.follower.id = :userId")
    long getFollowingCount(
            @Param("userId") UUID userId);

    Optional<Follow> findByFolloweeIdAndFollowerId(UUID followeeId, UUID followerId);

    long countByFolloweeId(UUID followeeId);
    long countByFollowerId(UUID followerId);
}
