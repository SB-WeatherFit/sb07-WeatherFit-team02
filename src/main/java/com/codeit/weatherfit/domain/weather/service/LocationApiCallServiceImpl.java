package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class LocationApiCallServiceImpl implements LocationApiCallService {

    private final WebClient kakaoClient;


    @Override
    public KakaoLocationResponse getKaKaoResponse(WeatherRequest request) {
        double longitude = request.longitude();
        double latitude = request.latitude();

        return kakaoClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .queryParam("input_coord", "WGS84")
                        .build())
                .retrieve()
                .bodyToMono(KakaoLocationResponse.class)
                .block();


    }

}
