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
    private static final QWeather weather = QWeather.weather;
    @Override
    public List<Weather> getWeatherByLocation(double longitude, double latitude, Instant forecastedAt) {

        return factory
                .selectFrom(weather)
                .where(weather.latitude.eq(latitude),
                        weather.longitude.eq(longitude),
                       weather.forecastedAt.eq(forecastedAt)
                    )
                .fetch();
    }

    @Override
    public Weather getSingleWeatherByLocation(double longitude, double latitude) {

        return factory
                .selectFrom(weather)
                .where(weather.latitude.eq(latitude),
                        weather.longitude.eq(longitude)
                )

                .fetchFirst();
    }


}
