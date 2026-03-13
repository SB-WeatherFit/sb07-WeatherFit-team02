package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
}
