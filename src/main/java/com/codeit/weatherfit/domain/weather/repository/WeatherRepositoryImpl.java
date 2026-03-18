package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.QWeather;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class WeatherRepositoryImpl implements WeatherRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Weather> getWeatherByLocation(double longitude, double latitude, Instant forecastedAt) {

        return factory
                .selectFrom(QWeather.weather)
                .where(QWeather.weather.latitude.eq(latitude),
                        QWeather.weather.longitude.eq(longitude),
                        QWeather.weather.forecastedAt.eq(forecastedAt)
                    )
                .fetch();
    }


}
