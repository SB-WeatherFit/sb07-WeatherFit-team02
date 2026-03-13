package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "feeds")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Weather weather;

    @NotNull
    @OneToMany
    private List<Ootd> ootds;

    @NotBlank
    private String content;

}
