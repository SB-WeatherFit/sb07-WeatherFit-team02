package com.codeit.weatherfit.domain.user.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(name = "email")
    private String email;
    private String name;
    private UserRole role;
    private String password;
    private boolean locked;

}
