package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationsSearchCondition;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static com.codeit.weatherfit.domain.notification.entity.QNotification.*;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notification> searchCursor(NotificationsSearchCondition condition) {
       return queryFactory.selectFrom(notification)
                .where(
                        cursorInstant(condition.cursor())
                )
                .orderBy(notification.createdAt.asc())
                .limit(condition.limit() + 1)
                .fetch();
    }

    private BooleanExpression cursorInstant(Instant cursor) {
        if (cursor == null) {
            return null;
        }
        return notification.createdAt.gt(cursor);
    }
}
