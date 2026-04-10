package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.recommendation.ai.AiClothesRecommender;
import com.codeit.weatherfit.domain.recommendation.dto.RecommendationDto;
import com.codeit.weatherfit.domain.recommendation.dto.RecommendedClothes;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ClothesRepository clothesRepository;
    private final WeatherRepository weatherRepository;
    private final ClothesRecommender clothesRecommender;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AiClothesRecommender aiClothesRecommender;

    @Override
    public RecommendationDto getRecommendations(UUID weatherId, UUID userId) {
        String key = "rec:pool:" + userId;
        List<UUID> result = null;

        Object redisSet = redisTemplate.opsForList().leftPop(key);

        if (redisSet instanceof List) {
            result = (List<UUID>) redisSet;

        } else {
            Weather weather = weatherRepository.findById(weatherId)
                    .orElseThrow(() -> new WeatherNotFoundException(weatherId));
            Profile profile = profileRepository.findByUserId(userId).orElseThrow();
            List<Clothes> clothes = clothesRepository.findByOwnerId(userId);

            if (clothes.isEmpty()) {
                return null;
            }

            List<List<UUID>> uuids = clothesRecommender.recommendClothes(clothes, weather, profile);

            if (uuids != null && !uuids.isEmpty()) {
                result = uuids.getFirst();

                if (uuids.size() > 1) {
                    List<List<UUID>> remainingSets = uuids.subList(1, uuids.size());
                    redisTemplate.opsForList().rightPushAll(key, remainingSets.toArray());


                    redisTemplate.expire(key, 1, TimeUnit.HOURS);
                }
            }
        }


        List<RecommendedClothes> list = clothesRepository.findAllByIds(result)
                .stream()
                .map(clothe -> new RecommendedClothes(
                                clothe.getId(),
                                clothe.getName(),
                                s3Service.getUrl(clothe.getImageKey()),
                                clothe.getType(),
                                List.of()
                        )
                )
                .toList();

        return new RecommendationDto(weatherId, userId, list);
    }

    @Override
    public RecommendationDto getRecommendationsLLM(UUID weatherId, UUID userId) {


        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> new WeatherNotFoundException(weatherId));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();
        List<Clothes> clothes = clothesRepository.findByOwnerId(userId);


        if (clothes.isEmpty()) {
            return null;
        }

        List<Clothes> filteredClothes = filterClothesByUserSensitivity(clothes, weather.getTemperatureCurrent(), profile.getTemperatureSensitivity());

        List<UUID> uuids = aiClothesRecommender.recommendClothes(filteredClothes, weather, profile);


        List<RecommendedClothes> list = clothesRepository.findAllByIds(uuids)
                .stream()
                .map(clothe -> new RecommendedClothes(
                                clothe.getId(),
                                clothe.getName(),
                                s3Service.getUrl(clothe.getImageKey()),
                                clothe.getType(),
                                List.of()
                        )
                )
                .toList();

        return new RecommendationDto(weatherId, userId, list);
    }

    private List<Clothes> filterClothesByUserSensitivity(List<Clothes> clothes, double currentTemperature, int temperatureSensitivity) {

        double effectiveTemp =
                switch (temperatureSensitivity) {
                    case 1 -> currentTemperature - 3;
                    case 2 -> currentTemperature - 1.5;
                    case 4 -> currentTemperature + 1.5;
                    case 5 -> currentTemperature + 3;
                    default -> currentTemperature;
                };


        return clothes.stream()
                .filter(clothe -> {
                    ClothesType type = clothe.getType();

                    if (effectiveTemp >= 25) {
                        return !List.of(ClothesType.OUTER, ClothesType.SCARF).contains(type);
                    }


                    if (effectiveTemp >= 23) {

                        return type != ClothesType.SCARF;
                    }


                    if (effectiveTemp <= 5) {
                        return true;
                    }

                    return true;
                })
                .toList();
    }
}

