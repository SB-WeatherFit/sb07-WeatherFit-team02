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
                        cursorCondition(request.cursor(), request.idAfter()),
                        keywordLike(request.keywordLike()),
                        skyStatusEq(request.skyStatusEqual()),
                        precipitationTypeEq(request.precipitationTypeEqual()),
                        authorIdEq(request.authorIdEqual())
                ).orderBy(createOrderSpecifier(request.sortBy(), request.sortDirection()))
                .limit(request.limit() + 1)
                .fetch();
    }

    /**
     *
     * @param sortBy
     * @param sortDirection
     * @return
     *
     * createdAt으로만 정렬하지 말고 아이디로도 정렬해라
     */
    private OrderSpecifier<?> createOrderSpecifier(SortBy sortBy, SortDirection sortDirection) {
        Order order = sortDirection == null || sortDirection == SortDirection.ASCENDING ?
                Order.ASC : Order.DESC;

        return switch (sortBy) {
            case createdAt -> new OrderSpecifier<>(order, feed.createdAt);
            case likeCount -> new OrderSpecifier<>(order,
                    JPAExpressions.select(feedLike.count())
                            .from(feedLike)
                            .where(feedLike.feed.eq(feed)));
        };
    }

    /**
     *
     * @param cursor
     * @param idAfter
     * @return
     * 마지막에 들어오는 쿼리로 해서 next 커서로 넘어가는게 20인데
     * 그 다음 쿼리가 실행될 때 where절이 createdAt을 50개 다 같으면
     * id에 대한 정렬이 없으면 문제가 발생함
     * 오름차순으로 정렬해서 lt가 아니라 gt를 써야한다
     */
    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter) {
        if (cursor == null || idAfter == null)
            return null;
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
