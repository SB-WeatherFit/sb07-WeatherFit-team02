package com.codeit.weatherfit.domain.auth.repository;

import com.codeit.weatherfit.domain.auth.entity.TemporaryPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPassword, UUID> {

    Optional<TemporaryPassword> findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(UUID userId);

    List<TemporaryPassword> findAllByUserIdAndUsedFalse(UUID userId);

    void deleteAllByUserIdAndUsedFalse(UUID userId);
}