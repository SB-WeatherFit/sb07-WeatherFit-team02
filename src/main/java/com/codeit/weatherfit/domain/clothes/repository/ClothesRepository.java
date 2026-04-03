package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ClothesRepository extends JpaRepository<Clothes, UUID>, ClothesRepositoryCustom {
    long countByOwner_Id(UUID ownerId);

    List<Clothes> findByOwner_IdOrderByCreatedAtDescIdDesc(
            UUID ownerId,
            Pageable pageable
    );

    List<Clothes> findByOwnerId(UUID ownerId);

    @Query("select c from Clothes c" +
            " where c.id in :clothesIds")
    List<Clothes> findAllByIds(
            @Param("clothesIds") List<UUID> clothesIds);

    @Query("select c.imageKey from Clothes c " +
            "where c.imageKey is not null")
    Set<String> findAllImageKeys();
}