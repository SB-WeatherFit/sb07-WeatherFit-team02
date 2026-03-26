package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.SortBy;
import com.codeit.weatherfit.domain.feed.dto.request.SortDirection;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.codeit.weatherfit.domain.feed.entity.QFeed.feed;
import static com.codeit.weatherfit.domain.feed.entity.QFeedLike.feedLike;
import static com.codeit.weatherfit.domain.user.entity.QUser.user;
import static com.codeit.weatherfit.domain.weather.entity.QWeather.weather;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Feed> findWithCursor(FeedGetRequest request) {
        return queryFactory
                .selectFrom(feed)
                .join(feed.author, user)
                .join(feed.weather, weather)
                .where(
                        cursorCondition(request.cursor(), request.idAfter(), request.sortDirection()),
                        keywordLike(request.keywordLike()),
                        skyStatusEq(request.skyStatusEqual()),
                        precipitationTypeEq(request.precipitationTypeEqual()),
                        authorIdEq(request.authorIdEqual())
                ).orderBy(createOrderSpecifier(request.sortBy(), request.sortDirection()))
                .limit(request.limit() + 1)
                .fetch();
    }

    private OrderSpecifier<?>[] createOrderSpecifier(SortBy sortBy, SortDirection sortDirection) {
        Order order = sortDirection == null || sortDirection == SortDirection.ASCENDING
                ? Order.ASC : Order.DESC;
        return switch (sortBy) {
            case createdAt -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(order, feed.createdAt),
                    new OrderSpecifier<>(order, feed.id)
            };
            case likeCount -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(order, JPAExpressions.select(feedLike.count())
                            .from(feedLike).where(feedLike.feed.eq(feed))),
                    new OrderSpecifier<>(order, feed.id)
            };
        };
    }

    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter, SortDirection direction) {
        if (cursor == null || idAfter == null) return null;
        if (direction == SortDirection.ASCENDING) {
            return feed.createdAt.gt(cursor)
                    .or(feed.createdAt.eq(cursor).and(feed.id.gt(idAfter)));
        }
        return feed.createdAt.lt(cursor)
                .or(feed.createdAt.eq(cursor).and(feed.id.lt(idAfter)));
    }

    private BooleanExpression skyStatusEq(SkyStatus skyStatus) {
        if (skyStatus == null)
            return null;
        return feed.weather.skyStatus.eq(skyStatus);
    }

    private BooleanExpression precipitationTypeEq(PrecipitationType precipitationType) {
        if (precipitationType == null)
            return null;
        return feed.weather.type.eq(precipitationType);
    }

    private BooleanExpression authorIdEq(UUID authorId) {
        if (authorId == null)
            return null;
        return feed.author.id.eq(authorId);
    }

    // TODO : 추후 개선
    private BooleanExpression keywordLike(String keyword) {
        if (keyword == null)
            return null;
        return feed.content.contains(keyword);
    }




}
