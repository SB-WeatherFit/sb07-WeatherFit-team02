package com.codeit.weatherfit.domain.weather.repository;

import com.codeit.weatherfit.domain.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


public interface WeatherRepository extends JpaRepository<Weather, UUID> ,WeatherRepositoryCustom {


}
