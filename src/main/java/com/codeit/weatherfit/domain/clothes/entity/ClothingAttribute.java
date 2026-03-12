package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "clothing_attributes")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothingAttribute extends BaseEntity {

    private String name;
    private String value;

}
