package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.codeit.weatherfit.domain.message.entity.QMessage.message;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Message> getByCursor(MessageGetRequest request) {
        return queryFactory.selectFrom(message)
                .where(cursorCondition(request.cursor(), request.idAfter()),
                        message.sender.id.eq(request.userId())
                                .or(message.receiver.id.eq(request.userId())))
                .orderBy(message.createdAt.desc(), message.id.desc())
                .limit(request.limit() + 1)
                .fetch();
    }

    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter) {
        if (cursor == null || idAfter == null)
            return null;
        return message.createdAt.lt(cursor)
                .or(message.createdAt.eq(cursor).and(message.id.lt(idAfter)));
    }
}
