package com.codeit.weatherfit.domain.feed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class OotdClothes {

    @Column(nullable = false)
    private String name;

    private String imageUrl;

}
