package com.codeit.weatherfit.domain.profile.location;

import com.codeit.weatherfit.domain.profile.entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileLocationResolver {

    private final KakaoRegionClient kakaoRegionClient;
    private final WeatherGridCoordinateCalculator weatherGridCoordinateCalculator;

    public Location resolve(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return Location.empty();
        }

        WeatherGridCoordinate gridCoordinate = weatherGridCoordinateCalculator.convert(latitude, longitude);
        List<String> locationNames = kakaoRegionClient.getLocationNames(longitude, latitude);

        return Location.create(
                latitude,
                longitude,
                gridCoordinate.x(),
                gridCoordinate.y(),
                locationNames
        );
    }
}