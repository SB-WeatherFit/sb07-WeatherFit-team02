package com.codeit.weatherfit.domain.follow.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.follow.exception.InvalidFollowArgumentException;
import com.codeit.weatherfit.domain.follow.exception.SelfFollowNotAllowedException;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    public static Follow create(FollowCreateParam followCreateParam) {
        User followee = followCreateParam.followee();
        User follower = followCreateParam.follower();

        validateUsersExist(followee, follower);
        validateNotSelfFollow(followee, follower);

        Follow follow = new Follow();
        follow.followee = followee;
        follow.follower = follower;

        return follow;
    }

    private static void validateUsersExist(User followee, User follower) {
        if (followee == null || follower == null) {
            throw new InvalidFollowArgumentException();
        }
    }

    private static void validateNotSelfFollow(User followee, User follower) {
        UUID followeeId = followee.getId();
        UUID followerId = follower.getId();

        if (followeeId != null && followeeId.equals(followerId)) {
            throw new SelfFollowNotAllowedException();
        }
    }
}
