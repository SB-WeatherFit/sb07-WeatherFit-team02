package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "feeds")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @ManyToOne
    private User author;

    private String weather; //todo : weather 타입 생성후 weather 타입 받기

    @OneToMany
    private List<Ootd> ootds;
    private String content;

}
