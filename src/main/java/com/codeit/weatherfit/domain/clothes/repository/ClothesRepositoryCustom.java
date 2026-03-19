package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ClothesRepositoryCustom {
    List<Clothes> search(
            UUID ownerId,
            Instant cursor,
            UUID idAfter,
            ClothesType type,
            int size
    );

    long count(UUID ownerId, ClothesType type);
}
