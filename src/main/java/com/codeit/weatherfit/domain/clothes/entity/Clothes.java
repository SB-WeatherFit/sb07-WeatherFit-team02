package com.codeit.weatherfit.domain.clothes.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private Clothes(User owner, String name, ClothesType type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    public static Clothes create(User owner, String name, ClothesType type) {
        return new Clothes(owner, name, type);
    }

    public void update(String name, ClothesType type) {
        this.name = name;
        this.type = type;
    }
}
