package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.event.weather.TemperatureNotificationEvent;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherRepository weatherRepository;
    private final WeatherApiCallService weatherApiCallService;
    private final Executor weatherUpdateTaskExecutor;
    private final Executor weatherDeleteTaskExecutor;
    private final ApplicationEventPublisher eventPublisher;


    public List<WeatherResponse> updateWeather() {
        List<Weather> allData = weatherRepository.findAll();

        Map<WeatherRequest, List<String>> locationData = allData.stream()
                .collect(Collectors.toMap(
                        w -> new WeatherRequest(w.getLongitude(), w.getLatitude()),
                        w -> List.of(w.getAddressFirst(), w.getAddressSecond(), w.getAddressThird()),
                        (existing, replacement) -> existing
                ));

        deleteAllWeatherCache();

        List<CompletableFuture<Void>> futures = locationData.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    List<WeatherResponse> response =
                            weatherApiCallService.getWeatherLisFromAdministration(
                                    entry.getKey(),
                                    Instant.now(),
                                    entry.getValue()
                            );

                    response.forEach(x -> {
                        weatherRepository.deleteOldForecast(
                                x.location().longitude(),
                                x.location().latitude(),
                                x.forecastAt()
                        );

                        weatherRepository.save(Weather.create(x));
                    });

                }, weatherUpdateTaskExecutor))
                .toList();


        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return weatherRepository.findAll().stream()
                .map(x -> WeatherResponse.from(x))
                .toList();


    }

    @CacheEvict(value = "weathers", allEntries = true)
    public void deleteAllWeatherCache() {
    }

    public void deleteWeather() {
        Instant targetTime = Instant.now().minus(1, ChronoUnit.HOURS);
        CompletableFuture.runAsync(() ->
                        weatherRepository.deleteOlderThen(targetTime)
                , weatherDeleteTaskExecutor);

        return;
    }

    public void weatherTemperatureNotification() {
        Instant currentTime = Instant.now();
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);
        List<Weather> allData = weatherRepository.findAll();
        Set<WeatherRequest> locationSet = new HashSet<>();
        allData.stream()
                .forEach(
                        data -> locationSet.add(
                                new WeatherRequest(data.getLongitude()
                                        , data.getLatitude()))
                );
        locationSet.stream()
                .forEach(

                        location -> {

                            Weather currentWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), currentTime);
                            Weather beforeWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), oneHourBefore);
                            if (currentWeather == null) {

                                log.warn("경도: {} 위도: {} 시간: {} data가 존재하지 않습니다.", location.longitude(), location.latitude(), currentTime.toString());
                                return;
                            }
                            if (beforeWeather == null) {

                                log.warn("경도: {} 위도: {} 시간: {} data가 존재하지 않습니다.", location.longitude(), location.latitude(), oneHourBefore.toString());
                                return;
                            }

                            double deltaTemperature = Math.abs(currentWeather.getTemperatureCurrent() - beforeWeather.getTemperatureCurrent());
                            if (deltaTemperature > 5.0) {
                                List<UUID> ids = List.of(); //todo 해당 위치 유저id 긁어오는 내용 추가
                                String content = "급격한 날씨 변화가 있을 예정입니다. 기온 변화를 주의하세요";

                                ids.forEach(
                                        x ->
                                                eventPublisher.publishEvent(new TemperatureNotificationEvent(
                                                        x,
                                                        content
                                                ))
                                );


                                //todo 여기서 event send
                            }
                        }
                );

    }

    public void weatherPrecipitationNotification() {
        Instant currentTime = Instant.now();
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);
        List<Weather> allData = weatherRepository.findAll();
        Set<WeatherRequest> locationSet = new HashSet<>();
        allData.stream()
                .forEach(
                        data -> locationSet.add(
                                new WeatherRequest(data.getLongitude()
                                        , data.getLatitude()))
                );
        locationSet.stream()
                .forEach(

                        location -> {
                            Weather currentWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), currentTime);
                            Weather beforeWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), oneHourBefore);

                            if (currentWeather == null) {

                                log.warn("경도: {} 위도: {} 시간: {} data가 존재하지 않습니다.", location.longitude(), location.latitude(), currentTime.toString());
                                return;
                            }
                            if (beforeWeather == null) {

                                log.warn("경도: {} 위도: {} 시간: {} data가 존재하지 않습니다.", location.longitude(), location.latitude(), oneHourBefore.toString());
                                return;
                            }
                            double deltaPrecipitation = currentWeather.getAmount() - beforeWeather.getAmount();
                            if (deltaPrecipitation > 5.0) {
                                List<UUID> ids = List.of(); //todo 해당 위치 유저id 긁어오는 내용 추가
                                String content = "비가 급격하게 쏟아집니다.";
                                ids.forEach(
                                        x ->
                                                eventPublisher.publishEvent(new TemperatureNotificationEvent(
                                                        x,
                                                        content
                                                ))
                                );
                            }
                        }
                );

    }

}
