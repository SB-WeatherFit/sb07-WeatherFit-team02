package com.codeit.weatherfit.domain.weather.dto.response;

import java.util.List;

public record KakaoLocationResponse(

    List<KakaoDocument> documents
) {
    public record KakaoDocument(
            String region_1depth_name,
            String region_2depth_name,
            String region_3depth_name,
            String x,
            String y
    ){}
}
