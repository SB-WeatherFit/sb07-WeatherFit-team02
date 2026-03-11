package com.codeit.weatherfit.domain.follow.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "followers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follower extends BaseEntity {

    @ManyToOne
    private User followee;

    @ManyToOne
    private User follower;
}
