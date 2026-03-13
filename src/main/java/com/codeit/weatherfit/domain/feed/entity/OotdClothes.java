package com.codeit.weatherfit.domain.feed.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Embeddable
@Getter
public class OotdClothes {

    @NotBlank
    private String name;

    private String imageUrl;

}
