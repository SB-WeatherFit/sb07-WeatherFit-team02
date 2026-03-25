package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.QSelectableValue;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import static com.codeit.weatherfit.domain.clothes.entity.QSelectableValue.selectableValue;
@RequiredArgsConstructor
public class SelectableValueRepositoryImpl implements SelectableValueRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public void deleteSelectableValuesByType(UUID typeId) {
         factory.
                delete(selectableValue)
                .where(
                        selectableValue.clothesAttributeType.id.eq(typeId)
                )
                .execute();
         return;
    }
}
