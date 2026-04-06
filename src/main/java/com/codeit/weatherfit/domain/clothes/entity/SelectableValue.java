package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Table(name = "selectable_values")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectableValue extends BaseEntity {
    // 빨주노초파남보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="type_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClothesAttributeType clothesAttributeType; // 색상
    private String option; // 빨강

    public static SelectableValue create(ClothesAttributeType type,String option){
        SelectableValue selectableValue = new SelectableValue();
        selectableValue.clothesAttributeType = type;
        selectableValue.option = option;
        return selectableValue;

    }
}
