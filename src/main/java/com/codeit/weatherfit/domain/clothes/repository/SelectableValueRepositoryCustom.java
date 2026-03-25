package com.codeit.weatherfit.domain.clothes.repository;

import java.util.UUID;

public interface SelectableValueRepositoryCustom {

    void deleteSelectableValuesByType(UUID typeId);
}
