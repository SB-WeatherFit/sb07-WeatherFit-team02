package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.QClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.QSelectableValue;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeit.weatherfit.domain.clothes.entity.QClothesAttributeType.*;
import static com.codeit.weatherfit.domain.clothes.entity.QSelectableValue.*;

@RequiredArgsConstructor
public class ClothesAttributeTypeRepositoryImpl implements ClothesAttributeTypeRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<ClothesAttributeDefDto> getAttributeDefs(SortBy sortBy, SortDirection sortDirection) {
        List<Tuple> rows = factory
                .select(clothesAttributeType, selectableValue)
                .from(clothesAttributeType)
                .leftJoin(selectableValue)
                .on(selectableValue.clothesAttributeType.eq(clothesAttributeType))
                .orderBy(getOrderSpecifier(sortBy, sortDirection))
                .fetch();

        Map<ClothesAttributeType, List<SelectableValue>> map = rows.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(clothesAttributeType),
                        Collectors.mapping(
                                t -> t.get(selectableValue),
                                Collectors.toList()
                        )

                ));
        return map.entrySet().stream()
                .map(e-> ClothesAttributeDefDto.from(e.getKey(),e.getValue()))
                .toList();

    }

    @Override
    public List<ClothesAttributeDefDto> getAttributeDefs(SortBy sortBy, SortDirection sortDirection, String keyword) {
        List<Tuple> rows = factory
                .select(clothesAttributeType, selectableValue)
                .from(clothesAttributeType)
                .leftJoin(selectableValue)
                .on(selectableValue.clothesAttributeType.eq(clothesAttributeType))
                .where(clothesAttributeType.name.contains(keyword))
                .orderBy(getOrderSpecifier(sortBy, sortDirection))
                .fetch();

        Map<ClothesAttributeType, List<SelectableValue>> map = rows.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(clothesAttributeType),
                        Collectors.mapping(
                                t -> t.get(selectableValue),
                                Collectors.toList()
                        )

                ));
        return map.entrySet().stream()
                .map(e-> ClothesAttributeDefDto.from(e.getKey(),e.getValue()))
                .toList();

    }

    private OrderSpecifier<?> getOrderSpecifier(SortBy sortBy, SortDirection sortDirection) {

        PathBuilder<ClothesAttributeType> pathBuilder =
                new PathBuilder<>(ClothesAttributeType.class, "clothesAttributeType");
        return sortDirection == SortDirection.ASCENDING
                ? pathBuilder.getComparable(sortBy.getValue(), Comparable.class).asc()
                : pathBuilder.getComparable(sortBy.getValue(), Comparable.class).desc();
    }
}
