package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "clothes_attribute_types")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothesAttributeType extends BaseEntity {
    // 색상, 사이즈, 소재...

    private String name;

    public void updateName(String name){
        this.name=name;
    }

    public static ClothesAttributeType create(String name){
        ClothesAttributeType attributeType = new ClothesAttributeType();
        attributeType.name = name;
        return attributeType;
    }

}