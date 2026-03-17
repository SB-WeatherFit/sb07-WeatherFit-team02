package com.codeit.weatherfit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClient weatherApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .defaultHeader("Content-Type", "application/json")
                .build();

    }

    @Bean
    public WebClient weatherAdministrationClient() {
        return WebClient.builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
