package com.codeit.weatherfit.domain.clothes.entity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "selectable_values")
public class SelectableValue {

    @ManyToOne
    private ClothingAttributeType clothingAttributeType;
    private String value;
}
