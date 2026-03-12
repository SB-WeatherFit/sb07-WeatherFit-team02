package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.clothes.entity.ClothingType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
public class OotdClothes {

    private String name;
    private String imageUrl;

}
