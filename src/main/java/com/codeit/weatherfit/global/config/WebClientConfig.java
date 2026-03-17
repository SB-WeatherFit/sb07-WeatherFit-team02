package com.codeit.weatherfit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${kakao.api-key}")
    private String kakaoApiKey;

    @Bean
    public WebClient weatherAdministrationClient() {
        return WebClient.builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient kakaoClient() {
        System.out.println(kakaoApiKey);
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK "+ kakaoApiKey)
                .build();
    }
}
