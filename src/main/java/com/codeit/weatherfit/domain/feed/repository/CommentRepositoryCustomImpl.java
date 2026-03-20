package com.codeit.weatherfit.domain.feed.repository;

import com.codeit.weatherfit.domain.feed.dto.request.CommentGetRequest;
import com.codeit.weatherfit.domain.feed.entity.Comment;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.codeit.weatherfit.domain.feed.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getCommentsByCursor(CommentGetRequest request) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.author).fetchJoin()
                .where(cursorCondition(request.cursor(), request.idAfter()),
                        comment.feed.id.eq(request.feedId()))
                .orderBy(new OrderSpecifier<>(Order.DESC, comment.createdAt))
                .limit(request.limit() + 1)
                .fetch();
    }

    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter) {
        if (cursor == null || idAfter == null)
            return null;
        return comment.createdAt.lt(cursor)
                .or(comment.createdAt.eq(cursor).and(comment.id.lt(idAfter)));
    }
}
