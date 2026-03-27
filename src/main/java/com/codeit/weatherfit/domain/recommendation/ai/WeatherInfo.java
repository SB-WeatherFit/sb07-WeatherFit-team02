package com.codeit.weatherfit.domain.recommendation.ai;

import com.codeit.weatherfit.domain.weather.entity.Weather;

public record WeatherInfo(
        double currentTemperature,   // 현재 온도 (가장 중요: 반팔 vs 패딩)
        double maxTemperature,       // 최고 온도 (일교차 확인용)
        double minTemperature,       // 최저 온도 (아침/저녁 쌀쌀함 확인용)
        String skyStatus,            // 하늘 상태 (맑음, 구름많음 등)
        String precipitationType,    // 강수 형태 (없음, 비, 눈, 소나기)
        double precipitationProbability, // 강수 확률 (우산이나 기능성 외투 추천용)
        double windSpeed,            // 바람 속도 (체감 온도를 낮추는 요인)
        String address               // "서울시 강남구" 정도의 위치 (계절감 보정용)
) {
    // Weather 엔티티로부터 변환하는 정적 팩토리 메서드를 두면 관리가 편합니다.
    public static WeatherInfo from(Weather weather) {
        return new WeatherInfo(
                weather.getTemperatureCurrent(),
                weather.getMax(),
                weather.getMin(),
                weather.getSkyStatus().name(),
                weather.getType().name(),
                weather.getProbability(),
                weather.getSpeed(),
                weather.getAddressFirst() + " " + weather.getAddressSecond()
        );
    }
}
