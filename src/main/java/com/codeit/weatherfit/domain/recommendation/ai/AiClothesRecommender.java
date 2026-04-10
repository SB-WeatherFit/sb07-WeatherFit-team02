package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.recommendation.service.ClothesRecommender;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AiClothesRecommender{

    private final ChatClient chatClient;

    public AiClothesRecommender(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }


    public List<UUID> recommendClothes(List<Clothes> clothesList, Weather weather, Profile profile) {

        List<PromptClothesInfo> list = clothesList.stream()
                .map(PromptClothesInfo::from)
                .toList();

        WeatherInfo weatherInfo = WeatherInfo.from(weather); // todo: 우선은 고정된 날씨 정보 전달
//        WeatherInfo weatherInfo = new WeatherInfo(12, 13, 10, SkyStatus.CLEAR.name(), PrecipitationType.NONE.name(), 3.3, 10, "서울");
        UserInfo userInfo = UserInfo.from(profile);

        var outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<ClothesSetResponse>() {});

        String systemMessage = ClothesRecommendationPrompt.systemPrompt();
        String userMessage = ClothesRecommendationPrompt.userPrompt(list, weatherInfo, userInfo);

        ClothesSetResponse entity = chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .entity(outputConverter);

        if(entity==null) {
            throw new RuntimeException("");
        }

        return entity.items();
    }
}
