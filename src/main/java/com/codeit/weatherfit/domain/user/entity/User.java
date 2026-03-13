package com.codeit.weatherfit.domain.user.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    private User(String email, String name, UserRole role, String password, boolean locked) {
        validateEmail(email);
        validateName(name);
        validateRole(role);
        validatePassword(password);

        this.email = email;
        this.name = name;
        this.role = role;
        this.password = password;
        this.locked = locked;
    }

    public static User create(String email, String name, UserRole role, String password) {
        return new User(email, name, role, password, false);
    }

    public void changeRole(UserRole role) {
        validateRole(role);
        this.role = role;
    }

    public void changePassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    public void changeLockState(boolean locked) {
        this.locked = locked;
    }

    private static void validateEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }

    private static void validateName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    private static void validateRole(UserRole role) {
        if (Objects.isNull(role)) {
            throw new IllegalArgumentException("role must not be null");
        }
    }

    private static void validatePassword(String password) {
        if (Objects.isNull(password) || password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }
}