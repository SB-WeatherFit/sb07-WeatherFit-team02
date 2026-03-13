package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApiCallServiceImpl implements WeatherApiCallService {

    private final WebClient webClient;

    @Value("${open-weather-map.api-key}")
    private String apiKey;

    private final String apiLanguage = "kr";
    @Override
    public WeatherApiResponse getWeathersFromNow(LocationResponse location) {
        double latitude = location.latitude();
        double longitude = location.longitude();

        long before = System.currentTimeMillis();
        WeatherApiResponse result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", apiKey)
                        .queryParam("lang", apiLanguage)
                        .queryParam("units", "metric")
                        .build()
                )
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .block();
        long after = System.currentTimeMillis();

        log.info("spending time: {} sec", (after - before) / 1000.0);
        return result;
    }
}
