package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class WeatherRepositoryImpl implements WeatherRepositoryCustom {

    private final JPAQueryFactory factory;
    @Override
    public List<WeatherResponse> getWeatherByLocation(WeatherRequest request, Instant time) {
        return List.of();
    }

    @Override
    public boolean isDataExist(WeatherRequest request, Instant time) {
        return false;
    }
}
