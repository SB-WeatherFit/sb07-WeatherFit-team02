package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClothesAttributeRepository extends JpaRepository <ClothesAttribute, UUID> {
    Optional<ClothesAttribute> findByClothesAndOption_ClothesAttributeType(
            Clothes clothes,
            ClothesAttributeType type
    );
    List<ClothesAttribute> findByClothes(Clothes clothes);

    void deleteByClothes(Clothes clothes);

    @Modifying
    @Query("delete from ClothesAttribute ca where ca.option.clothesAttributeType.id = :defId")
    void deleteByAttributeType(UUID defId);

    @Query(
            "select ca.option.option from ClothesAttribute ca " +
                    "where ca.clothes = :clothes")
    List<String> getClothesOptions(@Param("clothes") Clothes clothes);

}
