package com.codeit.weatherfit.domain.profile.location;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoRegionClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${kakao.api-key}")
    private String kakaoApiKey;

    public List<String> getLocationNames(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return List.of();
        }

        if (kakaoApiKey == null || kakaoApiKey.isBlank()) {
            return List.of();
        }

        try {
            KakaoCoordToRegionResponse response = restClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("dapi.kakao.com")
                            .path("/v2/local/geo/coord2regioncode.json")
                            .queryParam("x", longitude)
                            .queryParam("y", latitude)
                            .queryParam("input_coord", "WGS84")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .body(KakaoCoordToRegionResponse.class);

            if (response == null || response.documents() == null || response.documents().isEmpty()) {
                return List.of();
            }

            KakaoCoordToRegionResponse.KakaoRegionDocument firstDocument = response.documents().get(0);

            List<String> locationNames = new ArrayList<>();
            addIfPresent(locationNames, firstDocument.region_1depth_name());
            addIfPresent(locationNames, firstDocument.region_2depth_name());
            addIfPresent(locationNames, firstDocument.region_3depth_name());

            return locationNames;
        } catch (Exception exception) {
            return List.of();
        }
    }

    private void addIfPresent(List<String> locationNames, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        locationNames.add(value);
    }
}