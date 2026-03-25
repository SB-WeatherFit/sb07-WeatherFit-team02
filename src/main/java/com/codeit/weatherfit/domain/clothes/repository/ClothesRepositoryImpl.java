package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.clothes.entity.QClothes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
public class ClothesRepositoryImpl implements ClothesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long count(UUID ownerId, ClothesType type) {
        QClothes clothes = QClothes.clothes;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(clothes.owner.id.eq(ownerId));

        if (type != null) {
            builder.and(clothes.type.eq(type));
        }

        return queryFactory
                .select(clothes.count())
                .from(clothes)
                .where(builder)
                .fetchOne();
    }

    @Override
    public List<Clothes> search(
            UUID ownerId,
            Instant cursor,
            UUID idAfter,
            ClothesType type,
            int size
    ) {
        QClothes clothes = QClothes.clothes;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(clothes.owner.id.eq(ownerId));

        if (type != null) {
            builder.and(clothes.type.eq(type));
        }

        if (cursor != null && idAfter != null) {
            builder.and(
                    clothes.createdAt.lt(cursor)
                            .or(
                                    clothes.createdAt.eq(cursor)
                                            .and(clothes.id.lt(idAfter))
                            )
            );
        }

        return queryFactory
                .selectFrom(clothes)
                .where(builder)
                .orderBy(
                        clothes.createdAt.desc(),
                        clothes.id.desc()
                )
                .limit(size + 1)
                .fetch();
    }
}
