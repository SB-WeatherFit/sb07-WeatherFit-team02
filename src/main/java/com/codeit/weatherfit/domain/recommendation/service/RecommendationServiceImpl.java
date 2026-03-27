package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;

import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.recommendation.ai.ClothesRecommender;
import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final ClothesRecommender clothesRecommender;
    private final ProfileRepository profileRepository;

    @Override
    public RecommendationDto getRecommendations(UUID weatherId, UUID userId) {
        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> new WeatherNotFoundException(weatherId));


        Profile profile = profileRepository.findByUserId(userId).orElseThrow();

        List<Clothes> clothes = clothesRepository.findByOwnerId(userId);

        List<UUID> uuids = clothesRecommender.recommendClothes(clothes, weather, profile);
        System.out.println("uuids = " + uuids);



        return null;
    }
}
