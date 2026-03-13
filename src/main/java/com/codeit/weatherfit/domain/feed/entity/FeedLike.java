package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feeds_likes")
public class FeedLike extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Feed feed;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}
