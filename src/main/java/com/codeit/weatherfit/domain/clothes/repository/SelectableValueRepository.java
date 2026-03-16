package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import org.hibernate.mapping.Selectable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SelectableValueRepository extends JpaRepository<SelectableValue, UUID> {
}
