package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.profile.entity.QProfile;
import com.codeit.weatherfit.domain.user.entity.QUser;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.QWeather;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.codeit.weatherfit.domain.profile.entity.QProfile.profile;
import static com.codeit.weatherfit.domain.user.entity.QUser.user;
import static com.codeit.weatherfit.domain.weather.entity.QWeather.weather;

@RequiredArgsConstructor
public class WeatherRepositoryImpl implements WeatherRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public List<Weather> getWeatherByLocation(double longitude, double latitude, Instant forecastedAt) {
        return factory
                .selectFrom(weather)
                .where(weather.latitude.eq(latitude),
                        weather.longitude.eq(longitude),
                        weather.forecastedAt.eq(forecastedAt.truncatedTo(ChronoUnit.MICROS))
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

    @Override
    public Weather getSingleWeather(double longitude, double latitude, Instant time) {
        return factory
                .selectFrom(weather)
                .where(weather.latitude.eq(latitude),
                        weather.longitude.eq(longitude),
                        weather.forecastedAt.eq(time.truncatedTo(ChronoUnit.MICROS))
                )
                .fetchFirst();
    }

    @Override
    public List<Weather> getWeatherByLocationAndForecastAt(double longitude, double latitude, Instant forecastAt) {
        return factory
                .selectFrom(weather)
                .where(weather.latitude.eq(latitude),
                        weather.longitude.eq(longitude),
                        weather.forecastAt.eq(forecastAt.truncatedTo(ChronoUnit.MICROS))
                )
                .fetch();
    }

    @Override
    public void deleteOldForecast(double longitude, double latitude, Instant forecastAt) {
        factory.
                delete(weather)
                .where(
                        weather.longitude.eq(longitude),
                        weather.longitude.eq(latitude),
                        weather.forecastAt.eq(forecastAt.truncatedTo(ChronoUnit.MICROS))

                )
                .execute();
    }

    @Override
    public void deleteOlderThen(Instant forecastAt) {
        factory
                .delete(weather)
                .where(weather.forecastedAt.lt(forecastAt))
                .execute();

    }

    @Override
    public List<UUID> getTemperatureNotificationTarget() {
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.HOURS);
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);

        QWeather weather1 = weather;
        QWeather weather2 = new QWeather("weather2");
        QProfile profile = QProfile.profile;

        return factory
                .select(profile.user.id)
                .from(weather1)
                .join(weather2)
                .on(
                        weather1.latitude.eq(weather2.latitude),
                        weather1.longitude.eq(weather2.longitude)
                )
                .join(profile)
                .on(
                        profile.location.latitude.eq(weather1.latitude),
                        profile.location.longitude.eq(weather1.longitude)
                )
                .where(
                        weather1.forecastAt.eq(currentTime),
                        weather2.forecastAt.eq(oneHourBefore),
                        weather1.temperatureCurrent
                                .subtract(weather2.temperatureCurrent)
                                .abs()
                                .gt(5.0)
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<UUID> getHumidityNotificationTarget() {
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.HOURS);
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);

        QWeather weather1 = weather;
        QWeather weather2 = new QWeather("weather2");
        QProfile profile = QProfile.profile;

        return factory
                .select(profile.user.id)
                .from(weather1)
                .join(weather2)
                .on(
                        weather1.latitude.eq(weather2.latitude),
                        weather1.longitude.eq(weather2.longitude)
                )
                .join(profile)
                .on(
                        profile.location.latitude.eq(weather1.latitude),
                        profile.location.longitude.eq(weather1.longitude)
                )
                .where(
                        weather1.forecastAt.eq(currentTime),
                        weather2.forecastAt.eq(oneHourBefore),
                        weather1.amount
                                .subtract(weather2.amount)
                                .gt(5.0)
                )
                .distinct()
                .fetch();
    }
}
