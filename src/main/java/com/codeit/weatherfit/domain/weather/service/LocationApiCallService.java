package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;

public interface LocationApiCallService {

    KakaoLocationResponse getKaKaoResponse(WeatherRequest weatherRequest);
}
