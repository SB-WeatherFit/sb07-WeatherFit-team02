package com.codeit.weatherfit.domain.weather.service.integration;

import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import com.codeit.weatherfit.domain.weather.service.WeatherApiCallService;
import com.codeit.weatherfit.domain.weather.util.TestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WeatherApiCallServiceImplTest {

    @Autowired
    private WeatherApiCallService weatherApiCallService;

    @Autowired
    TestFixture testFixture;




}