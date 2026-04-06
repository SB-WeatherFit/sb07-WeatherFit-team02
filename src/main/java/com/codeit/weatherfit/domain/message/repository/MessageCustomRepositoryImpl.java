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
public class MessageCustomRepositoryImpl implements MessageCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Message> searchMessages(MessageGetRequest request, UUID senderId) {
        return queryFactory.selectFrom(message)
                .where(
                        cursorCondition(request.cursor(), request.idAfter()),
                        (message.receiver.id.eq(request.userId()).and(message.sender.id.eq(senderId)))
                                .or(message.receiver.id.eq(senderId).and(message.sender.id.eq(request.userId()))))
                .orderBy(message.createdAt.desc(), message.id.asc())
                .limit(request.limit() + 1)
                .fetch();
    }

    private BooleanExpression cursorCondition(Instant cursor, UUID idAfter) {
        if (cursor == null || idAfter == null)
            return null;
        return message.createdAt.lt(cursor)
                .or(message.createdAt.eq(cursor).and(message.id.gt(idAfter)));
    }
}
