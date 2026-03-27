package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.repository.WeatherRepository;
import com.codeit.weatherfit.domain.weather.util.TestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("[날씨 서비스 유닛 테스트]")
class WeatherServiceSliceTest {

    @Mock
    WeatherApiCallService weatherApiCallService;

    @InjectMocks
    WeatherServiceImpl weatherService;

    @Mock
    WeatherRepository weatherRepository;

    @Mock
    LocationApiCallService locationApiCallService;

    TestFixture testFixture = new TestFixture();

    @Test
    @DisplayName("[정상 케이스] 날씨 정보 조회 및 생성")
    void valid_createWeather(){

        WeatherRequest request = testFixture.requestWeatherFactory();
        WeatherResponse weatherResponse = testFixture.weatherFactory();

        List<WeatherResponse> dummyResponse = List.of(weatherResponse);
        Instant time = Instant.now();

        KakaoLocationResponse.KakaoDocument kakaoDocument = testFixture.kakaoDocumentFactory();

        //given
        given(weatherRepository.getWeatherByLocation(
                any(Double.class),
                any(Double.class),
                any(Instant.class)))
                .willReturn(List.of());
        given(weatherRepository.save(any())).willReturn(Weather.create(weatherResponse));

        given(weatherApiCallService.getWeatherLisFromAdministration(
                any(WeatherRequest.class),any(Instant.class),anyList()
        )).willReturn(dummyResponse);

        given(locationApiCallService.getKaKaoResponse(any(WeatherRequest.class)))
                .willReturn(new KakaoLocationResponse(
                        List.of(kakaoDocument)
                ));

        //when
        List<WeatherResponse> result = weatherService.create(request);

        //then

        then(weatherRepository).should(times(1))
                .getWeatherByLocation(any(Double.class),any(Double.class),any(Instant.class));
        then(weatherApiCallService).should(times(1))
                .getWeatherLisFromAdministration(any(WeatherRequest.class),any(Instant.class),anyList());
        then(locationApiCallService).should(times(1))
                .getKaKaoResponse(any(WeatherRequest.class));

        assertThat(result.getFirst().location()).isEqualTo(weatherResponse.location());

    }

    @Test
    @DisplayName("[정상 케이스] 날씨 위치 정보 조회")
    void valid_getWeatherLocation(){
        WeatherRequest request = testFixture.requestWeatherFactory();
        WeatherResponse weatherResponse = testFixture.weatherFactory();

        given(weatherRepository.getSingleWeatherByLocation(

                any(Double.class),any(Double.class)
        )).willReturn(Weather.create(weatherResponse));

        LocationResponse result = weatherService.getWeatherLocation(request);

        then(weatherRepository).should(times(1))
                .getSingleWeatherByLocation(any(Double.class),any(Double.class));
        assertThat(result).isEqualTo(weatherResponse.location());

    }

}