package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "selectable_values")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectableValue extends BaseEntity {

    @ManyToOne
    private ClothesAttributeType clothesAttributeType;
    private String value;
}
