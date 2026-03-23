package com.codeit.weatherfit.domain.user.repository;

import com.codeit.weatherfit.domain.profile.entity.QProfile;
import com.codeit.weatherfit.domain.user.entity.QUser;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> searchUsers(UserSearchCondition condition) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(
                        emailLikeContains(condition.emailLike()),
                        roleEquals(condition.roleEqual()),
                        lockedEquals(condition.locked()),
                        cursorCondition(
                                condition.cursor(),
                                condition.idAfter(),
                                condition.sortBy(),
                                condition.sortDirection()
                        )
                )
                .orderBy(
                        primaryOrderSpecifier(condition.sortBy(), condition.sortDirection()),
                        secondaryIdOrderSpecifier(condition.sortDirection())
                )
                .limit((long) condition.limit() + 1)
                .fetch();
    }

    @Override
    public long countUsers(UserSearchCondition condition) {
        QUser user = QUser.user;

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        emailLikeContains(condition.emailLike()),
                        roleEquals(condition.roleEqual()),
                        lockedEquals(condition.locked())
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    @Override
    public List<UUID> getUserIdsByLocation(double longitude, double latitude) {
        QProfile profile = QProfile.profile;
        QUser user = QUser.user;

        return queryFactory
                .select(user.id)
                .from(profile)
                .join(profile.user, user)
                .where(
                        profile.location.latitude.eq(latitude),
                        profile.location.longitude.eq(longitude)
                )
                .fetch();
    }

    private BooleanExpression emailLikeContains(String emailLike) {
        if (emailLike == null || emailLike.isBlank()) {
            return null;
        }

        return QUser.user.email.containsIgnoreCase(emailLike);
    }

    private BooleanExpression roleEquals(UserRole roleEqual) {
        if (roleEqual == null) {
            return null;
        }

        return QUser.user.role.eq(roleEqual);
    }

    private BooleanExpression lockedEquals(Boolean locked) {
        if (locked == null) {
            return null;
        }

        return QUser.user.locked.eq(locked);
    }

    private BooleanBuilder cursorCondition(
            String cursor,
            UUID idAfter,
            String sortBy,
            String sortDirection
    ) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        String normalizedSortBy = normalizeSortBy(sortBy);
        String normalizedSortDirection = normalizeSortDirection(sortDirection);

        if ("email".equals(normalizedSortBy)) {
            return emailCursorCondition(cursor, idAfter, normalizedSortDirection);
        }

        return createdAtCursorCondition(cursor, idAfter, normalizedSortDirection);
    }

    private BooleanBuilder createdAtCursorCondition(
            String cursor,
            UUID idAfter,
            String sortDirection
    ) {
        QUser user = QUser.user;
        Instant cursorValue = Instant.parse(cursor);

        BooleanBuilder builder = new BooleanBuilder();

        if ("ASCENDING".equals(sortDirection)) {
            builder.and(
                    user.createdAt.gt(cursorValue)
                            .or(user.createdAt.eq(cursorValue).and(idCompareGreater(idAfter)))
            );
            return builder;
        }

        builder.and(
                user.createdAt.lt(cursorValue)
                        .or(user.createdAt.eq(cursorValue).and(idCompareLess(idAfter)))
        );
        return builder;
    }

    private BooleanBuilder emailCursorCondition(
            String cursor,
            UUID idAfter,
            String sortDirection
    ) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if ("ASCENDING".equals(sortDirection)) {
            builder.and(
                    user.email.gt(cursor)
                            .or(user.email.eq(cursor).and(idCompareGreater(idAfter)))
            );
            return builder;
        }

        builder.and(
                user.email.lt(cursor)
                        .or(user.email.eq(cursor).and(idCompareLess(idAfter)))
        );
        return builder;
    }

    private BooleanExpression idCompareGreater(UUID idAfter) {
        if (idAfter == null) {
            return null;
        }

        return QUser.user.id.gt(idAfter);
    }

    private BooleanExpression idCompareLess(UUID idAfter) {
        if (idAfter == null) {
            return null;
        }

        return QUser.user.id.lt(idAfter);
    }

    private OrderSpecifier<?> primaryOrderSpecifier(String sortBy, String sortDirection) {
        String normalizedSortBy = normalizeSortBy(sortBy);
        Order order = toOrder(sortDirection);

        if ("email".equals(normalizedSortBy)) {
            return new OrderSpecifier<>(order, QUser.user.email);
        }

        return new OrderSpecifier<>(order, QUser.user.createdAt);
    }

    private OrderSpecifier<?> secondaryIdOrderSpecifier(String sortDirection) {
        return new OrderSpecifier<>(toOrder(sortDirection), QUser.user.id);
    }

    private String normalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }

        if ("email".equals(sortBy)) {
            return "email";
        }

        return "createdAt";
    }

    private String normalizeSortDirection(String sortDirection) {
        if ("ASCENDING".equals(sortDirection)) {
            return "ASCENDING";
        }

        return "DESCENDING";
    }

    private Order toOrder(String sortDirection) {
        if ("ASCENDING".equals(sortDirection)) {
            return Order.ASC;
        }

        return Order.DESC;
    }
}