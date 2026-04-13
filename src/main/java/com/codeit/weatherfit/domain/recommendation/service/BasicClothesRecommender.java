package com.codeit.weatherfit.domain.recommendation.service;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.recommendation.ai.WeatherInfo;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BasicClothesRecommender implements ClothesRecommender {
    @Override
    public List<List<UUID>> recommendClothes(List<Clothes> clothesDtoList, Weather weather, Profile profile) {
        WeatherInfo weatherInfo = WeatherInfo.from(weather);
        double temperature = weatherInfo.currentTemperature();

        Map<ClothesType, List<Clothes>> clothesMap = clothesDtoList.stream()
                .collect(Collectors.groupingBy(Clothes::getType));

        List<List<UUID>> totalSets = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<UUID> oneSet = createRandomSet(clothesMap, temperature);
            if (!oneSet.isEmpty()) totalSets.add(oneSet);
        }
        return totalSets;
    }

    private List<UUID> createRandomSet(Map<ClothesType, List<Clothes>> map, double temp) {
        List<UUID> set = new ArrayList<>();
        Random random = new Random();

        // 1. 메인 의상 결정 (원피스 vs 상의+하의)
        // 원피스가 있고, 30% 확률 혹은 상하의가 없을 때 원피스 선택
        boolean isDressSelected = false;
        if (map.containsKey(ClothesType.DRESS) && random.nextDouble() < 0.3) {
            set.add(getRandomId(map.get(ClothesType.DRESS), random));
            isDressSelected = true;
        }

        // 원피스를 안 입었을 때만 상/하의를 추가
        if (!isDressSelected) {
            addIfPresent(set, map, ClothesType.TOP, 1, random);
            addIfPresent(set, map, ClothesType.BOTTOM, 1, random);
        }

        addIfPresent(set, map, ClothesType.SHOES, 1.0, random);
        addIfPresent(set, map, ClothesType.UNDERWEAR, 1.0, random);

        // 3. 날씨에 따른 필수/확률 추가 (아우터, 스카프)
        if (temp < 10.0) { // 추우면 아우터 필수
            addIfPresent(set, map, ClothesType.OUTER, 1.0, random);
            addIfPresent(set, map, ClothesType.SCARF, 0.7, random);
        } else if (temp < 20.0) { // 선선하면 아우터 50% 확률
            addIfPresent(set, map, ClothesType.OUTER, 0.5, random);
        }

        // 4. 패션의 완성: 순수 확률형 악세사리
        addIfPresent(set, map, ClothesType.SOCKS, 0.8, random);     // 양말 80%
        addIfPresent(set, map, ClothesType.BAG, 0.6, random);       // 가방 60%
        addIfPresent(set, map, ClothesType.HAT, 0.2, random);       // 모자 20%
        addIfPresent(set, map, ClothesType.ACCESSORY, 0.4, random); // 악세사리 40%
        addIfPresent(set, map, ClothesType.ETC, 0.1, random);       // 기타 10%

        return set;
    }

    private void addIfPresent(List<UUID> set, Map<ClothesType, List<Clothes>> map,
                              ClothesType type, double probability, Random random) {
        if (map.containsKey(type) && random.nextDouble() < probability) {
            set.add(getRandomId(map.get(type), random));
        }
    }

    private UUID getRandomId(List<Clothes> list, Random random) {
        return list.get(random.nextInt(list.size())).getId();
    }
}
