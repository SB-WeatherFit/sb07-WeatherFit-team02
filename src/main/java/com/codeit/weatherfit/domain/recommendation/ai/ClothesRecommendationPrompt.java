package com.codeit.weatherfit.domain.recommendation.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class ClothesRecommendationPrompt {


    public static String systemPrompt() {
        return """
                너는 날씨와 사용자 정보를 기반으로 옷을 추천하는 패션 스타일리스트다.
                
                [핵심 규칙]
                1. 반드시 주어진 [옷 목록]에 있는 clothesId(UUID)만 사용해라.
                2.. 코디는 현실적인 조합이어야 한다 (예: 상의+하의+신발+외투).
                4. 같은 카테고리의 옷을 중복해서 입히지 마라 (예: 상의 2개 금지). 단, 레이어드(셔츠 위 니트 등)가 필요한 날씨라면 예외적으로 허용한다.
                5. 날씨(온도, 강수 여부)와 사용자의 온도 민감도를 최우선으로 고려해라.
                """;
    }

    public static String userPrompt(List<PromptClothesInfo> clothesInfos, WeatherInfo weatherInfo, UserInfo userInfo) {

        ObjectMapper objectMapper = new ObjectMapper();

        String clothesJson = null;
        String userJson = null;
        String weatherJson = null;
        try {
            clothesJson = objectMapper.writeValueAsString(clothesInfos);
            userJson = objectMapper.writeValueAsString(userInfo);
            weatherJson = objectMapper.writeValueAsString(weatherInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ;


        return """
                [날씨]
                %s
                
                [사용자]
                %s
                
                [옷 목록]
                %s
                
                위 정보를 기반으로 적절한 옷을 추천해라.
                """.formatted(weatherJson, userJson, clothesJson);
    }
}