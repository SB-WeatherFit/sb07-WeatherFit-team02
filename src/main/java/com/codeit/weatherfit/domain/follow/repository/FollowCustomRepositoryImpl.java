package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

import static com.codeit.weatherfit.domain.follow.entity.QFollow.follow;
import static com.codeit.weatherfit.domain.user.entity.QUser.user;


@RequiredArgsConstructor
public class FollowCustomRepositoryImpl implements FollowCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Follow> searchFollowees(FolloweeSearchCondition condition) {
        return queryFactory
                .select(follow)
                .from(follow)
                .join(follow.followee, user).fetchJoin()
                .where(
                        follow.follower.id.eq(condition.followerId()),
                        cursorCondition(condition.cursor()),
                        nameLike(condition.nameLike()))
                .orderBy(follow.createdAt.asc())
                .limit(condition.limit() + 1)
                .fetch();
    }

    @Override
    public List<Follow> searchFollowers(FollowerSearchCondition condition) {
        return queryFactory
                .select(follow)
                .from(follow)
                .join(follow.follower, user).fetchJoin()
                .where(
                        follow.followee.id.eq(condition.followeeId()),
                        cursorCondition(condition.cursor()),
                        nameLike(condition.nameLike()))
                .orderBy(follow.createdAt.asc())
                .limit(condition.limit() + 1)
                .fetch();
    }

    private BooleanExpression nameLike(String nameLike) {
        if (nameLike == null) {
            return null;
        }
        return follow.follower.name.like(nameLike);
    }

    private BooleanExpression cursorCondition(Instant cursor) {
        if (cursor == null) {
            return null;
        }
        return follow.createdAt.gt(cursor);
    }
}
