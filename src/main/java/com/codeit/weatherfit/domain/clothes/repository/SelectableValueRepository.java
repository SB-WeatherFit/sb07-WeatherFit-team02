package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SelectableValueRepository extends JpaRepository<SelectableValue, UUID>,SelectableValueRepositoryCustom {
    List<SelectableValue> findByClothesAttributeTypeAndOptionIn(
            ClothesAttributeType type,
            List<String> options
    );

    List<SelectableValue> findByClothesAttributeType(ClothesAttributeType type);

    Optional<SelectableValue> findByClothesAttributeTypeAndOption(ClothesAttributeType type, String value);

    @Query("select sv from SelectableValue sv" +
            " where sv.clothesAttributeType.id = :clothesAttributeTypeId")
    List<SelectableValue> findByClothesAttributeTypeId(
            @Param("clothesAttributeTypeId") UUID clothesAttributeTypeId);
}
