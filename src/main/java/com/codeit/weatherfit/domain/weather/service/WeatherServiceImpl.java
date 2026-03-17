package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiCallService weatherApiCallService;
    private final WeatherRepository weatherRepository;

    @Override
    public WeatherResponse getWeather(WeatherRequest request, Instant time) throws InterruptedException {
        return weatherApiCallService.getWeatherFromAdministration(request,time);


    }

    @Override
    public LocationResponse getWeatherLocation(WeatherRequest request) {
        return null;
    }

    @Override
    public List<WeatherResponse> create(WeatherRequest request, Instant time) {
        List<WeatherResponse> dtoLis = weatherApiCallService.getWeatherLisFromAdministration(request, time);
        List<WeatherResponse> result = new ArrayList<>();
        for (WeatherResponse weatherResponse : dtoLis) {
            Weather weather = weatherRepository.save(Weather.create(weatherResponse));
            result.add(WeatherResponse.from(weather));
        }
        return result;
    }


    @Override
    public void delete(UUID id) {
        if(!weatherRepository.existsById(id)) throw new WeatherNotFoundException(id);
        weatherRepository.deleteById(id);
    }

    @Override
    public WeatherResponse getWeather(UUID id) {
        Weather weather = weatherRepository.findById(id).orElseThrow(() -> new WeatherNotFoundException(id));
        return WeatherResponse.from(weather);
    }
}
