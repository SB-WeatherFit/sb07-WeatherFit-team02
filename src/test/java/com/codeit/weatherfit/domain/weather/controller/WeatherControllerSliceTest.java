package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.service.LocationApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherScheduler;
import com.codeit.weatherfit.domain.weather.service.WeatherService;
import com.codeit.weatherfit.domain.weather.util.TestFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver; // 추가
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

@DisplayName("[날씨 컨트롤러 유닛 테스트]")
class WeatherControllerSliceTest {

    private WeatherService weatherService;
    private WeatherApiCallServiceImpl weatherApiCallService;
    private LocationApiCallServiceImpl locationApiCallService;
    private WeatherScheduler weatherScheduler;

    private MockMvcTester tester;
    private final TestFixture testFixture = new TestFixture();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        weatherService = Mockito.mock(WeatherService.class);
        weatherApiCallService = Mockito.mock(WeatherApiCallServiceImpl.class);
        locationApiCallService = Mockito.mock(LocationApiCallServiceImpl.class);
        weatherScheduler = Mockito.mock(WeatherScheduler.class);

        WeatherController weatherController = new WeatherController(
                weatherService,
                weatherApiCallService,
                locationApiCallService,
                weatherScheduler
        );

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(weatherController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()) // 추가
                .build();

        tester = MockMvcTester.create(mockMvc);
    }

    @Test
    @DisplayName("[정상 케이스] 날씨 정보 조회 및 생성")
    void valid_createWeather() {
        WeatherRequest request = testFixture.requestWeatherFactory();
        WeatherResponse weatherResponse = testFixture.weatherFactory();
        List<WeatherResponse> dummyResponse = List.of(weatherResponse);

        given(weatherService.create(any(WeatherRequest.class)))
                .willReturn(dummyResponse);

        assertThat(tester.get()
                .uri("/api/weathers")
                .param("latitude", String.valueOf(request.latitude()))
                .param("longitude", String.valueOf(request.longitude())))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].id").asString().isEqualTo(weatherResponse.id().toString());

        then(weatherService).should(times(1))
                .create(any(WeatherRequest.class));
    }

    @Test
    @DisplayName("[정상 케이스] 날씨 위치 정보 조회")
    void valid_getWeatherLocation() {
        WeatherRequest request = testFixture.requestWeatherFactory();
        LocationResponse locationResponse = testFixture.locationFactory();

        given(weatherService.getWeatherLocation(any(WeatherRequest.class)))
                .willReturn(locationResponse);

        assertThat(tester.get()
                .uri("/api/weathers/location")
                .param("latitude", String.valueOf(request.latitude()))
                .param("longitude", String.valueOf(request.longitude())))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.x").isEqualTo(locationResponse.x());

        then(weatherService).should(times(1))
                .getWeatherLocation(any(WeatherRequest.class));
    }
}