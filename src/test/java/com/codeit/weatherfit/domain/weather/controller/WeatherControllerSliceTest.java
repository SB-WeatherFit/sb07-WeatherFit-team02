package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.service.LocationApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherScheduler;
import com.codeit.weatherfit.domain.weather.service.WeatherService;
import com.codeit.weatherfit.domain.weather.util.TestFixture;
import com.codeit.weatherfit.global.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.INSTANT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(WeatherController.class)
@Import(SecurityConfig.class)
@DisplayName("[날씨 컨트롤러 유닛 테스트]")
class WeatherControllerSliceTest {

    @MockitoBean
    WeatherService weatherService;

    @MockitoBean
    WeatherApiCallServiceImpl weatherApiCallService;

    @MockitoBean
     LocationApiCallServiceImpl locationApiCallService;

    @MockitoBean
    WeatherScheduler weatherScheduler;

    @Autowired
    MockMvcTester tester;

    @Autowired
    ObjectMapper objectMapper;

    TestFixture testFixture = new TestFixture();

    @Test
    @DisplayName("[정상 케이스] 날씨 정보 조회 및 생성")
    void valid_createWeather(){
        WeatherRequest request = testFixture.requestWeatherFactory();
        WeatherResponse weatherResponse = testFixture.weatherFactory();
        List<WeatherResponse> dummyResponse = List.of(weatherResponse);
        given(weatherService.create(any(
                WeatherRequest.class
        )
        )).willReturn(dummyResponse);

        assertThat(tester.get()
                .uri("/api/weathers")
                .param("latitude",String.valueOf(request.latitude()))
                .param("longitude",String.valueOf(request.longitude()))
        )
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].id").asString().isEqualTo(weatherResponse.id().toString());

        then(weatherService).should(times(1))
                .create(any(WeatherRequest.class));

    }

    @Test
    @DisplayName("[정상 케이스] 날씨 위치 정보 조회")
    void valid_getWeatherLocation(){
        WeatherRequest request = testFixture.requestWeatherFactory();
        LocationResponse locationResponse = testFixture.locationFactory();
        given(weatherService.getWeatherLocation(any(WeatherRequest.class)))
                .willReturn(locationResponse);

        assertThat(tester.get()
                .uri("/api/weathers/location")
                .param("latitude",String.valueOf(request.latitude()))
                .param("longitude",String.valueOf(request.longitude()))
        )
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.x").isEqualTo(locationResponse.x());

        then(weatherService).should(times(1))
                .getWeatherLocation(any(WeatherRequest.class));
    }

}