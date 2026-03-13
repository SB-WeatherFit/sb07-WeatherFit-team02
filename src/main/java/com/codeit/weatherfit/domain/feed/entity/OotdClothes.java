package com.codeit.weatherfit.domain.feed.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class OotdClothes {

    private String name;
    private String imageUrl;

}
