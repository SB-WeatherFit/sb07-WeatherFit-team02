package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationApiCallServiceImpl implements LocationApiCallService {

    private final WebClient kakaoClient;


    @Override
    public KakaoLocationResponse getKaKaoResponse(WeatherRequest request) {
        double longitude = request.longitude();
        double latitude = request.latitude();
        log.info("Kakao location request received. longitude: {}, latitude: {}", longitude, latitude);
        return kakaoClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .queryParam("input_coord", "WGS84")
                        .build())
                .retrieve()
                .bodyToMono(KakaoLocationResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnError(e-> log.error("KaKao api call fail ",e))
                .block();

    }

}
