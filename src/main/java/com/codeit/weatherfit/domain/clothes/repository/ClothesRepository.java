package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClothesRepository extends JpaRepository<Clothes, UUID>, ClothesRepositoryCustom {
    long countByOwner_Id(UUID ownerId);

    List<Clothes> findByOwner_IdOrderByCreatedAtDescIdDesc(
            UUID ownerId,
            Pageable pageable
    );
}