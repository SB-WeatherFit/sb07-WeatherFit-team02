package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AiClothesRecommender implements ClothesRecommender {

    private final ChatClient chatClient;

    public AiClothesRecommender(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @Override
    public List<UUID> recommendClothes(List<Clothes> clothesList, Weather weather, Profile profile) {

        List<PromptClothesInfo> list = clothesList.stream()
                .map(PromptClothesInfo::from)
                .toList();
        WeatherInfo weatherInfo = WeatherInfo.from(weather);
        UserInfo userInfo = UserInfo.from(profile);

        var outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ClothesSetResponse>>() {});

        String systemMessage = ClothesRecommendationPrompt.systemPrompt() + "\n" + outputConverter.getFormat();
        String userMessage = ClothesRecommendationPrompt.userPrompt(list, weatherInfo, userInfo);

        List<ClothesSetResponse> entity = chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .entity(outputConverter);


        return entity.get(0).items();
    }
}
