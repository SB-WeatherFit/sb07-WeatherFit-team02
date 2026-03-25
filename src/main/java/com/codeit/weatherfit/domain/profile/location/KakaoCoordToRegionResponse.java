package com.codeit.weatherfit.domain.profile.location;

import java.util.List;

public record KakaoCoordToRegionResponse(
        List<KakaoRegionDocument> documents
) {
    public record KakaoRegionDocument(
            String region_type,
            String address_name,
            String region_1depth_name,
            String region_2depth_name,
            String region_3depth_name,
            String region_4depth_name,
            Double x,
            Double y
    ) {
    }
}