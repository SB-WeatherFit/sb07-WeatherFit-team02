package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.weather.entity.Weather;

import java.util.List;
import java.util.UUID;

public interface ClothesRecommender {
    List<UUID> recommendClothes(List<Clothes> clothesDtoList, Weather weather, Profile profile);
}
