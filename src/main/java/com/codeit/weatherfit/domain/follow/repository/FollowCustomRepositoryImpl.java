package com.codeit.weatherfit.domain.follow.repository;

import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.entity.Follow;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
                        cursorCondition(condition.cursor(), condition.idAfter()),
                        nameLikeFollowee(condition.nameLike()))
                .orderBy(follow.createdAt.asc(), follow.id.asc())
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
                        cursorCondition(condition.cursor(), condition.idAfter()),
                        nameLikeFollowers(condition.nameLike()))
                .orderBy(follow.createdAt.asc(), follow.id.asc())
                .limit(condition.limit() + 1)
                .fetch();
    }

    private BooleanExpression nameLikeFollowee(String nameLike) {
        if (nameLike == null || nameLike.isBlank()) {
            return null;
        }
        return follow.followee.name.contains(nameLike);
    }

    private BooleanExpression nameLikeFollowers(String nameLike) {
        if (nameLike == null) {
            return null;
        }
        return follow.follower.name.contains(nameLike);
    }

    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter) {
        if (cursor == null) {
            return null;
        }
        return follow.createdAt.gt(cursor).or(follow.createdAt.eq(cursor).and(follow.id.gt(idAfter)));
    }
}
