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
}
