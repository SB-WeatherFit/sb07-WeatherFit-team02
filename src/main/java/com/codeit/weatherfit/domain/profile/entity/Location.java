package com.codeit.weatherfit.domain.profile.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public record Location(
        double latitude,
        double longitude,
        String address
) {

}
