package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.QClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.codeit.weatherfit.domain.clothes.entity.QClothesAttributeType.*;

@RequiredArgsConstructor
public class ClothesAttributeTypeRepositoryImpl implements ClothesAttributeTypeRepositoryCustom{

    private final JPAQueryFactory factory;

    @Override
    public List<ClothesAttributeType> getAttributeDefs(SortBy sortBy, SortDirection sortDirection, String keyword) {
        return factory
                .selectFrom(clothesAttributeType)
                .orderBy(getOrderSpecifier(sortBy,sortDirection))
                .fetch();

    }

    private OrderSpecifier<?> getOrderSpecifier(SortBy sortBy, SortDirection sortDirection) {

        PathBuilder<SelectableValue> pathBuilder =
                new PathBuilder<>(SelectableValue.class,"selectableValue");
        return sortDirection==SortDirection.ASCENDING
                ? pathBuilder.getComparable(sortBy.getValue(),Comparable.class).asc()
                : pathBuilder.getComparable(sortBy.getValue(),Comparable.class).desc();
    }
}
