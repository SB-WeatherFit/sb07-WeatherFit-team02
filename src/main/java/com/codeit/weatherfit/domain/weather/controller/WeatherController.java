package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weathers")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public void getWeather(@RequestParam double longitude, @RequestParam double latitude) {


    }

    @GetMapping("/location")
    public void getWeatherLocation(@RequestParam double longitude, @RequestParam double latitude) {

    }
}
