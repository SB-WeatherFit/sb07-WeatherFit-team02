package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsById(UUID userId);

    boolean existsByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseAndRoleAndLockedOrderByCreatedAtDescIdDesc(
            String emailLike,
            UserRole role,
            boolean locked,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseAndRoleOrderByCreatedAtDescIdDesc(
            String emailLike,
            UserRole role,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseAndLockedOrderByCreatedAtDescIdDesc(
            String emailLike,
            boolean locked,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
            String emailLike,
            Pageable pageable
    );

    long countByEmailContainingIgnoreCaseAndRoleAndLocked(String emailLike, UserRole role, boolean locked);

    long countByEmailContainingIgnoreCaseAndRole(String emailLike, UserRole role);

    long countByEmailContainingIgnoreCaseAndLocked(String emailLike, boolean locked);

    long countByEmailContainingIgnoreCase(String emailLike);

    List<User> findByEmailContainingIgnoreCaseAndRoleAndLockedAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
            String emailLike,
            UserRole role,
            boolean locked,
            Instant cursor,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseAndRoleAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
            String emailLike,
            UserRole role,
            Instant cursor,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseAndLockedAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
            String emailLike,
            boolean locked,
            Instant cursor,
            Pageable pageable
    );

    List<User> findByEmailContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(
            String emailLike,
            Instant cursor,
            Pageable pageable
    );
}