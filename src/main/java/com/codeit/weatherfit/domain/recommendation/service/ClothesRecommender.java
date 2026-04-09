package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.util.List;
import java.util.UUID;

public interface ClothesRecommender {
    List<List<UUID>> recommendClothes(List<Clothes> clothesDtoList, Weather weather, Profile profile);
}
