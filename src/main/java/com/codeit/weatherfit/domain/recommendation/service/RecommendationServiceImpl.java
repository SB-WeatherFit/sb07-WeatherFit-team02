package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.recommendation.ai.ClothesRecommender;
import com.codeit.weatherfit.domain.recommendation.dto.AttributesDto;
import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;
import com.codeit.weatherfit.domain.recommendation.dto.RecommendedClothes;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
//        Weather weather = weatherRepository.findById(weatherId)
//                .orElseThrow(() -> new WeatherNotFoundException(weatherId));
        //todo : 우선은 날씨 정보 전달 x

        Profile profile = profileRepository.findByUserId(userId).orElseThrow();

        List<Clothes> clothes = clothesRepository.findByOwnerId(userId);
        System.out.println("clothes = " + clothes);
        if(clothes.isEmpty()) {
            return null;
        }

//        List<UUID> uuids = clothesRecommender.recommendClothes(clothes, weather, profile);
        List<UUID> uuids = clothesRecommender.recommendClothes(clothes, null, profile);
        System.out.println("llm 조회 성공 uuids = " + uuids);

        List<RecommendedClothes> list = clothesRepository.findAllByIds(uuids)
                .stream()
                .filter(Objects::nonNull)
                .map(clothe -> new RecommendedClothes(
                        clothe.getId(),
                        clothe.getName(),
                        clothe.getImageKey(),
                        clothe.getType(),
                        List.of()
                        )
                )
                .toList();

        return new RecommendationDto(weatherId, userId, list);
    }
}
