package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {

    boolean existsById(UUID userId);

    boolean existsByEmail(String email);
}