package com.codeit.weatherfit.domain.recommendation.ai;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class ClothesRecommendationPrompt {


    public static String systemPrompt() {
        return """
                너는 날씨와 사용자 정보를 기반으로 옷을 추천하는 패션 스타일리스트다.
                
                [핵심 규칙]
                1. 반드시 주어진 [옷 목록]에 있는 clothesId(UUID)만 사용해라. 없는 아이디를 지어내지 마라.
                2. 사용자의 나이(age)가 0이거나 제공되지 않으면 20대 중반 성인을 기준으로 추천해라.
                3. 코디는 현실적인 조합이어야 한다 (예: 상의+하의+신발+외투).
                4. 같은 카테고리의 옷을 중복해서 입히지 마라 (예: 상의 2개 금지). 단, 레이어드(셔츠 위 니트 등)가 필요한 날씨라면 예외적으로 허용한다.
                5. 날씨(온도, 강수 여부)와 사용자의 온도 민감도를 최우선으로 고려해라.
                
                [응답 규칙]
                - 반드시 아래 제공되는 JSON 스키마 형식에 맞춰 응답해라.
                - JSON 데이터 외에 어떠한 설명, 인삿말, 마크다운 코드 블록(```json 등)도 포함하지 마라.
                """;
    }

    public static String userPrompt(List<PromptClothesInfo> clothesInfos, WeatherInfo weatherInfo, UserInfo userInfo) {

        ObjectMapper objectMapper = new ObjectMapper();

        String clothesJson = objectMapper.writeValueAsString(clothesInfos);
        String userJson = objectMapper.writeValueAsString(userInfo);
        String weatherJson = objectMapper.writeValueAsString(weatherInfo);


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