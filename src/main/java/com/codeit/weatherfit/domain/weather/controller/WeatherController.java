package com.codeit.weatherfit.domain.weather.controller;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherApiTestRequest;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import com.codeit.weatherfit.domain.weather.service.LocationApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherApiCallServiceImpl;
import com.codeit.weatherfit.domain.weather.service.WeatherScheduler;
import com.codeit.weatherfit.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weathers")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherApiCallServiceImpl  weatherApiCallService;
    private final LocationApiCallServiceImpl locationApiCallService;
    private final WeatherScheduler weatherScheduler;

    @GetMapping
    public ResponseEntity<List<WeatherResponse>> createWeather(WeatherRequest weatherRequest) {

        List<WeatherResponse> response = weatherService.create(weatherRequest);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<WeatherResponse>> getWeather(@AuthenticationPrincipal UserDetails userDetails){

        //todo 현재 접속중인 유저 id 조회
        List<WeatherResponse> response = weatherService.getDefaultOrUserWeather(null);
        return ResponseEntity.ok().body(response);


    }

    @GetMapping("/location")
    public ResponseEntity<LocationResponse> getWeatherLocation(WeatherRequest weatherRequest) {

        LocationResponse response = weatherService.getWeatherLocation(weatherRequest);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<List<WeatherAdministrationTime>> test(WeatherApiTestRequest request) {
        List<WeatherAdministrationTime> response = weatherApiCallService.apiTest(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test/kakao")
    public ResponseEntity<KakaoLocationResponse> testKakao(WeatherRequest request){
        KakaoLocationResponse response = locationApiCallService.getKaKaoResponse(request);
        return ResponseEntity.ok().body(response);
    }



    @GetMapping("/test/scheduler/update")
    public ResponseEntity<List<WeatherResponse>> testUpdateScheduler(){
        List<WeatherResponse> response = weatherScheduler.updateWeather();
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("/test/scheduler/delete")
//    public ResponseEntity<List<WeatherResponse>> testDeleteScheduler(){
//        List<WeatherResponse> response = weatherScheduler.deleteWeather();
//        return ResponseEntity.ok().body(response);
//    }






}
