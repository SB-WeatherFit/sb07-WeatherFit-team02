package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "clothes_attributes")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothesAttribute extends BaseEntity {
    // 맨투맨 Clothes 가 빨강을 갖고있다

    @ManyToOne(fetch = FetchType.LAZY)
    private Clothes clothes; // 맨투맨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="value_id")
    private SelectableValue option; // 빨강

    public void changeOption(SelectableValue option) { this.option = option; }

    public static ClothesAttribute create(Clothes clothes, SelectableValue option) {
        ClothesAttribute clothesAttribute = new ClothesAttribute();
        clothesAttribute.clothes = clothes;
        clothesAttribute.option = option;
        return clothesAttribute;
    }
}
