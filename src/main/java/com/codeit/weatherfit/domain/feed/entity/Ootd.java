package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ootds")
public class Ootd extends BaseEntity {

    @ManyToOne
    private Feed feed;

    @Embedded
    private OotdClothes clothes;

}
