package com.codeit.weatherfit.domain.weather.util;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.*;
import com.codeit.weatherfit.domain.weather.entity.AsWord;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class TestFixture {

    public LocationResponse locationFactory() {
        double randomLongitude = Math.random() * 360 - 180;
        double randomLatitude = Math.random() * 180 - 90;
        int x = (int) randomLongitude;
        int y = (int) randomLatitude;
        List<String> locationNames = List.of("서울특별시","서초구","반포동");

        return new LocationResponse(
                randomLatitude,
                randomLongitude,
                x,
                y,
                locationNames
        );


    }

    public WeatherResponse weatherFactory(){
        return new WeatherResponse(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                locationFactory(),
                SkyStatus.CLEAR,
                precipitationFactory(),
                humidityFactory(),
                temperatureFactory(),
                windSpeedFactory()
        );
    }

    public TemperatureResponse temperatureFactory(){

        return new TemperatureResponse(
                randomDouble(),
                randomDouble(),
                randomDouble(),
                randomDouble()
        );
    }
    public WeatherRequest requestWeatherFactory(){
        return new WeatherRequest(
                randomDouble(),
                randomDouble()
        );
    }

    public HumidityResponse humidityFactory(){

        return new HumidityResponse(
                randomDouble(),
                randomDouble()
        );
    }

    public PrecipitaionResponse  precipitationFactory(){

        return new PrecipitaionResponse(
                PrecipitationType.NONE,
                randomDouble(),
                randomDouble()

        );
    }

    public WindSpeedResponse windSpeedFactory(){

        return new WindSpeedResponse(
                randomDouble(),
                AsWord.WEAK
        );
    }
    public KakaoLocationResponse.KakaoDocument kakaoDocumentFactory(){
        return new KakaoLocationResponse.KakaoDocument(
                randomString(),
                randomString(),
                randomString(),
                String.valueOf(randomDouble()),
                String.valueOf(randomDouble())
        );
    }

    private double randomDouble() {
        return Math.random() * 100;
    }

    private int randomInt(){
        return (int) (Math.random() * 3);
    }

    private String randomString(){
        return UUID.randomUUID().toString();
    }


}