package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClothesAttributeTypeRepository extends JpaRepository<ClothesAttributeType, UUID> ,ClothesAttributeTypeRepositoryCustom{
    Optional<ClothesAttributeType> findByName(String name);
}
