package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "clothes")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clothes extends BaseEntity {


    @ManyToOne
    private User owner;

    private String name;
    private String imageUrl;
    private ClothesType type;

    public Clothes(User owner, String name, ClothesType type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

}
