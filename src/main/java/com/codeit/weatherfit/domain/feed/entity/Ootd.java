package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ootds")
public class Ootd extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Feed feed;

    @Embedded
    @NotNull
    private OotdClothes clothes;

}
