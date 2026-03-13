package com.codeit.weatherfit.domain.weather.util;

import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import org.springframework.stereotype.Component;

@Component
public class TestFixture {

    public LocationResponse locationFactory(){
        double randomLongitude = Math.random()*360 -180;
        double randomLatitude = Math.random()*180 -90;
        String address = "서울특별시";
        return new LocationResponse(
                randomLatitude,
                randomLongitude,
                address
        );
    }
}
