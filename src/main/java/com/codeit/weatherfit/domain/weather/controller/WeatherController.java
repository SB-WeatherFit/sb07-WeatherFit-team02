package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/weathers")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(WeatherRequest weatherRequest) throws InterruptedException {

        WeatherResponse response = weatherService.getWeather(weatherRequest, Instant.now());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/location")
    public void getWeatherLocation(WeatherRequest weatherRequest) {

    }



}
