package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import com.codeit.weatherfit.domain.weather.service.WeatherApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/weathers")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherApiCallServiceImpl weatherApiCallService;

    @GetMapping
    public void getWeather(WeatherRequest weatherRequest) {


    }

    @GetMapping("/location")
    public void getWeatherLocation(WeatherRequest weatherRequest) {

    }

    @GetMapping("/test")
    public ResponseEntity<WeatherApiResponse> getWeatherTest(WeatherRequest weatherRequest) {

        WeatherApiResponse response = weatherService.getWeatherApiResponse(weatherRequest);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test/administration")
    public ResponseEntity<WeatherResponse> getAdministration(WeatherRequest weatherRequest) throws InterruptedException {
        WeatherResponse response = weatherApiCallService.getWeatherFromAdministration(weatherRequest, Instant.now());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test/realTest")
    public ResponseEntity<List<WeatherAdministrationTime>> getRealTest(WeatherRequest weatherRequest) {
        List<WeatherAdministrationTime> response = weatherApiCallService.realTest(weatherRequest);
        return ResponseEntity.ok().body(response);

    }
}
