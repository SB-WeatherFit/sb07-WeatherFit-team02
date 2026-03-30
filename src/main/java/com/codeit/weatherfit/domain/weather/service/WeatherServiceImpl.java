package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.exception.WeatherNotFoundException;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiCallService weatherApiCallService;
    private final WeatherRepository weatherRepository;
    private final LocationApiCallService locationApiCallService;
    private static final double DEFAULT_LONGITUDE = 127.000749; // 디폴트 위치 값 (서울특별시 서초구 반포동)
    private static final double DEFAULT_LATITUDE = 37.503974;
    private final ProfileRepository profileRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");


    @Override
//    @Cacheable(value = "weathers", key = "#request.latitude() +':'+ #request.longitude")
    public List<WeatherResponse> create(WeatherRequest request) {
        Instant time = Instant.now();
        KakaoLocationResponse kaKaoResponse = locationApiCallService.getKaKaoResponse(request);
        var document = kaKaoResponse.documents().getFirst();
        double longitude = Double.parseDouble(document.x());
        double latitude = Double.parseDouble(document.y());
        List<String> address = List.of(document.region_1depth_name(), document.region_2depth_name(), document.region_3depth_name());
        WeatherRequest kakaoLocation = new WeatherRequest(longitude, latitude);

        List<Weather> dbData = getWeatherAsLis(kakaoLocation, time);
        if (!dbData.isEmpty()) {
            return dbData.stream()
                    .map(weather -> WeatherResponse.from(weather))
                    .toList();
        }
        List<WeatherResponse> dtoLis = weatherApiCallService.getWeatherLisFromAdministration(kakaoLocation, time, address);
        List<WeatherResponse> result = new ArrayList<>();
        for (WeatherResponse weatherResponse : dtoLis) {
            weatherRepository.deleteOldForecast(
                    weatherResponse.location().longitude(),
                    weatherResponse.location().latitude(),
                    weatherResponse.forecastAt()
            );
            Weather weather = weatherRepository.save(Weather.create(weatherResponse));
            result.add(WeatherResponse.from(weather));
        }
        return result;
    }

    @Override
    public LocationResponse getWeatherLocation(WeatherRequest request) {
        KakaoLocationResponse response = locationApiCallService.getKaKaoResponse(request);
        var document = response.documents().getFirst();
        double longitude = Double.parseDouble(document.x());
        double latitude = Double.parseDouble(document.y());
        List<String> address = List.of(
                document.region_1depth_name(),
                document.region_2depth_name(),
                document.region_3depth_name()
        );

        return new LocationResponse(
                latitude,
                longitude,
                (int) longitude,
                (int) latitude,
                address
        );
    }

    @Override
    public void delete(UUID id) {
        if (!weatherRepository.existsById(id)) throw new WeatherNotFoundException(id);
        weatherRepository.deleteById(id);
    }

    @Override
    public WeatherResponse getWeather(UUID id) {
        Weather weather = weatherRepository.findById(id).orElseThrow(() -> new WeatherNotFoundException(id));
        return WeatherResponse.from(weather);
    }

    @Override
    public List<WeatherResponse> getWeather(WeatherRequest request, Instant time) {

        List<Weather> weatherLis = getWeatherAsLis(request, time);
        return weatherLis.stream()
                .map(weather -> WeatherResponse.from(weather))
                .toList();
    }

    private Instant getForecastedAt(Instant targetTime) {
        List<Integer> timeLis = List.of(2, 5, 8, 11, 14, 17, 20, 23);
        ZonedDateTime zonedDateTime = targetTime.atZone(KST);

        int targetHour = zonedDateTime.getHour();
        int baseHour = timeLis.stream()
                .filter(hour -> hour <= targetHour)
                .max(Integer::compareTo)
                .orElse(23);


        ZonedDateTime forecastedAt = zonedDateTime.withHour(baseHour).withMinute(0).withSecond(0).withNano(0);
        if (targetHour < 2) forecastedAt = forecastedAt.minusDays(1);
        return forecastedAt.toInstant();
    }

    @Transactional(readOnly = true)
    public List<Weather> getWeatherAsLis(WeatherRequest request, Instant time) {
        Double longitude = request.longitude();
        Double latitude = request.latitude();
        Instant forecastedAt = getForecastedAt(time);
        List<Weather> weatherLis = weatherRepository.getWeatherByLocation(longitude, latitude, forecastedAt);

        return weatherLis;
    }

    @Override
    public List<WeatherResponse> getDefaultOrUserWeather(UUID userId) {
        if (userId == null) {

            List<WeatherResponse> response = create(new WeatherRequest(
                    DEFAULT_LONGITUDE,
                    DEFAULT_LATITUDE

            ));
            return response;
        }
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();
        return create(
                new WeatherRequest(
                        profile.getLocation().getLongitude(),
                        profile.getLocation().getLatitude()
                )
        );
    }
}
