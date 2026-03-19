package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import com.codeit.weatherfit.domain.weather.util.TestFixture;
import com.codeit.weatherfit.global.config.JpaAuditingConfig;
import com.codeit.weatherfit.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@DisplayName("[날씨 레포지토리 유닛 테스트]")
@Transactional
class WeatherRepositorySliceTest {

    private static final Logger log = LoggerFactory.getLogger(WeatherRepositorySliceTest.class);
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    EntityManager em;

    TestFixture testFixture= new TestFixture();

    @Test
    @DisplayName("[정상 케이스] 날씨 정보 조회")
    void valid_getWeatherData(){
        //given
        WeatherResponse weatherDto1 = testFixture.weatherFactory();
        WeatherResponse weatherDto2 = testFixture.weatherFactory();

        Weather weather1 = weatherRepository.save(Weather.create(weatherDto1));
        Weather weather2 = weatherRepository.save(Weather.create(weatherDto2));

        LocationResponse locationResponse1 = weatherDto1.location();
        Instant forecastedAt1 = weatherDto1.forecastedAt();


        LocationResponse locationResponse2 = weatherDto2.location();
        Instant forecastedAt2 = weatherDto2.forecastedAt();

        em.flush();
        em.clear();

        //when
        List<Weather> result1 = weatherRepository.getWeatherByLocation(
                locationResponse1.longitude(),
                locationResponse1.latitude(),
                forecastedAt1
        );

        List<Weather> result2= weatherRepository.getWeatherByLocation(
                locationResponse2.longitude(),
                locationResponse2.latitude(),
                forecastedAt2
        );


        //then
        assertThat(result1.getFirst().getId()).isEqualTo(weather1.getId());
        assertThat(result2.getFirst().getId()).isEqualTo(weather2.getId());
    }

    @Test
    @DisplayName("[정상 케이스] 날씨 위치 정보 조회")
    void valid_getWeatherLocationData(){

        //given
        WeatherResponse weatherDto1 = testFixture.weatherFactory();
        WeatherResponse weatherDto2 = testFixture.weatherFactory();

        Weather weather1 = weatherRepository.save(Weather.create(weatherDto1));
        Weather weather2 = weatherRepository.save(Weather.create(weatherDto2));

        LocationResponse locationResponse1 = weatherDto1.location();
        LocationResponse locationResponse2 = weatherDto2.location();

        //when
        Weather weatherResult = weatherRepository.getSingleWeatherByLocation(
                locationResponse1.longitude(),
                locationResponse1.latitude()
        );

        Weather weatherResult2 = weatherRepository.getSingleWeatherByLocation(
                locationResponse2.longitude(),
                locationResponse2.latitude()
        );

        //then
        assertThat(weatherResult.getId()).isEqualTo(weather1.getId());
        assertThat(weatherResult2.getId()).isEqualTo(weather2.getId());

    }


}