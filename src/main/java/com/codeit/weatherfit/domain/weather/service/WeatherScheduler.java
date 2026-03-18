package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherRepository weatherRepository;
    private final WeatherApiCallService weatherApiCallService;
    private final Executor weatherUpdateTaskExecutor;


    @Transactional
    public List<WeatherResponse> updateWeather() {
        List<Weather> allData = weatherRepository.findAll();

        Map<WeatherRequest, List<String>> locationData = allData.stream()
                .collect(Collectors.toMap(
                        w -> new WeatherRequest(w.getLongitude(), w.getLatitude()),
                        w -> List.of(w.getAddressFirst(), w.getAddressSecond(), w.getAddressThird()),
                        (existing, replacement) -> existing
                ));

        weatherRepository.deleteAll();
        deleteAllWeatherCache();

        List<CompletableFuture<Void>> futures = locationData.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    List<WeatherResponse> response =
                            weatherApiCallService.getWeatherLisFromAdministration(
                                    entry.getKey(),
                                    Instant.now(),
                                    entry.getValue());
                    response.forEach(x -> weatherRepository.save(Weather.create(x)));
                }, weatherUpdateTaskExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return weatherRepository.findAll().stream()
                .map(x-> WeatherResponse.from(x))
                .toList();


    }

    @CacheEvict(value="weathers",allEntries = true)
    public void deleteAllWeatherCache(){

    }

}
