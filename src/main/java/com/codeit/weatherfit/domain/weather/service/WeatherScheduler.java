package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherRepository weatherRepository;
    private final WeatherApiCallService weatherApiCallService;
    private final Executor weatherUpdateTaskExecutor;
    private final Executor weatherDeleteTaskExecutor;


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

    public List<WeatherResponse> deleteWeather(){
        Instant targetTime = Instant.now();
        List<Weather> allData = weatherRepository.findAll();
        CompletableFuture.runAsync(()->allData.stream()
                .filter(weather-> weather.getForecastAt().isBefore(targetTime.minus(1, ChronoUnit.HOURS)))// 알람 보내려면 1시간 전 데이터 가 필요해서 1시간 전 데이터 남겨둠
                .forEach(weatherRepository::delete),weatherDeleteTaskExecutor);
        return weatherRepository.findAll().stream()
                .map(x-> WeatherResponse.from(x))
                .toList();
    }

    public void weatherTemperatureNotification(){
        Instant currentTime = Instant.now();
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);
        List<Weather> allData = weatherRepository.findAll();
        Set<WeatherRequest> locationSet =new HashSet<>();
        allData.stream()
                .forEach(
                        data-> locationSet.add(
                                new WeatherRequest(data.getLongitude()
                                        , data.getLatitude()))
                );
        locationSet.stream()
                .forEach(

                        location->{
                            Weather currentWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), currentTime);
                            Weather beforeWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), oneHourBefore);

                            double deltaTemperature = Math.abs(currentWeather.getTemperatureCurrent() - beforeWeather.getTemperatureCurrent());
                            if(deltaTemperature>5.0){
                                List<UUID> ids; //todo 해당 위치 유저id 긁어오는 내용 추가
                                String title ;
                                String content= "급격한 온도 변화를 보였습니다.";
                                NotificationLevel level = NotificationLevel.WARNING;
                                //todo 여기서 event send
                            }
                        }
                );

    }

    public void weatherPrecipitationNotification(){
        Instant currentTime = Instant.now();
        Instant oneHourBefore = currentTime.minus(1, ChronoUnit.HOURS);
        List<Weather> allData = weatherRepository.findAll();
        Set<WeatherRequest> locationSet =new HashSet<>();
        allData.stream()
                .forEach(
                        data-> locationSet.add(
                                new WeatherRequest(data.getLongitude()
                                        , data.getLatitude()))
                );
        locationSet.stream()
                .forEach(

                        location->{
                            Weather currentWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), currentTime);
                            Weather beforeWeather = weatherRepository.getSingleWeather(location.longitude(), location.latitude(), oneHourBefore);

                            double deltaPrecipitation = currentWeather.getAmount() - beforeWeather.getAmount();
                            if(deltaPrecipitation>5.0){
                                List<UUID> ids; //todo 해당 위치 유저id 긁어오는 내용 추가
                                String title ;
                                String content= "비가 급격하게 쏟아집니다.";
                                NotificationLevel level = NotificationLevel.WARNING;
                                //todo 여기서 event send
                            }
                        }
                );

    }

}
