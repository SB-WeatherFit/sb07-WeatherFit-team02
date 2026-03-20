package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherApiTestRequest;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.*;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministration;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherCategoryType;
import com.codeit.weatherfit.domain.weather.entity.*;
import com.codeit.weatherfit.domain.weather.exception.WeatherCategoryNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApiCallServiceImpl implements WeatherApiCallService {

    private final WebClient weatherAdministrationClient;
    private static final String NO_RAIN_AMOUNT = "강수없음";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Value("${open-administration-map.api-key}")
    private String administrationApiKey;

    @Override
    public List<WeatherResponse> getWeatherLisFromAdministration(WeatherRequest request, Instant time,List<String> address) {
        long before = System.currentTimeMillis();

        LocalDateTime kst = LocalDateTime.ofInstant(time, KST);

        String baseDate = getBaseDate(kst);
        String yesterdayDate = getBaseDate(kst.minusDays(1));
        String tomorrowDate = getBaseDate(kst.plusDays(1));
        String secondDayDate = getBaseDate(kst.plusDays(2));
        String thirdDayDate = getBaseDate(kst.plusDays(3));
        String baseTime = getBaseTime(kst.toLocalTime());
        String baseTimeFor3Day = getBaseTimeFor3Day(kst.toLocalTime());
        String currentTime = getCurrentTime(time);

        Instant forecastedAt = getForecastedAt(time);
        LocationResponse tmpLocation = new LocationResponse(
                request.latitude(),
                request.longitude(),
                (int) request.latitude(),
                (int) request.longitude(),
                address
        );

        int[] convert = convertToGrid(request.latitude(), request.longitude());
        int nx = convert[0];
        int ny = convert[1];

        List<WeatherAdministrationTime> forecastedData = getWeatherFromApi(baseDate, baseTime, nx, ny, 13 * 24 * 4)
                .response()
                .body()
                .items()
                .item();

        List<WeatherAdministrationTime> yesterdayForecastedData = getWeatherFromApi(yesterdayDate, "0200", nx, ny, 13*24*2)
                .response()
                .body()
                .items()
                .item();
        Map<String, Map<String, String>> weatherMap = forecastedData.stream().collect(Collectors.groupingBy(
                item -> item.fcstDate() + item.fcstTime(),
                Collectors.toMap(
                        WeatherAdministrationTime::category,
                        WeatherAdministrationTime::fcstValue,
                        (a, b) -> a
                )

        ));

        Map<String, Map<String, String>> yesterDayWeatherMap = yesterdayForecastedData.stream().collect(Collectors.groupingBy(
                item -> item.fcstDate() + item.fcstTime(),
                Collectors.toMap(
                        WeatherAdministrationTime::category,
                        WeatherAdministrationTime::fcstValue,
                        (a, b) -> a
                )

        ));


        WeatherResponse todayResponse = getSingleWeatherResponseDto(
                weatherMap,
                yesterDayWeatherMap,
                baseDate,
                currentTime,
                yesterdayDate,
                forecastedAt,
                forecastedAt,
                tmpLocation

        );
        WeatherResponse tomorrowResponse = getSingleWeatherResponseDto(
                weatherMap,
                tomorrowDate,
                currentTime,
                baseDate,
                forecastedAt,
                forecastedAt.plus(1,ChronoUnit.DAYS),
                tmpLocation

        );

        WeatherResponse secondResponse = getSingleWeatherResponseDto(
                weatherMap,
                secondDayDate,
                currentTime,
                tomorrowDate,
                forecastedAt,
                forecastedAt.plus(2,ChronoUnit.DAYS),
                tmpLocation

        );

        WeatherResponse thirdResponse = getSingleWeatherResponseDto(
                weatherMap,
                thirdDayDate,
                baseTimeFor3Day,
                secondDayDate,
                forecastedAt,
                forecastedAt.plus(3,ChronoUnit.DAYS),
                tmpLocation

        );

        long after = System.currentTimeMillis();
        log.info("spending time to call list:{} seconds", (after - before) / 1000.0);

        return List.of(todayResponse, tomorrowResponse, secondResponse, thirdResponse);
    }
    private String getValue(Map<String, Map<String, String>> map,
                            String date, String time, WeatherCategoryType  category) {

        return Optional.ofNullable(map.get(date + time))
                .map(m -> m.get(category.name()))
                .orElseThrow(() -> new WeatherCategoryNotFoundException(
                       category));
    }

    public WeatherResponse getSingleWeatherResponseDto(
                Map<String,Map<String,String>> weatherMap,
                String date, String time,
                String yesterdayDate,
                Instant forecastedAt,
                Instant forecastAt,
                LocationResponse location
    ) {
        log.info("forecastAt: {}",forecastAt);
        String tmp = getValue(weatherMap, date, time, WeatherCategoryType.TMP);
        String yesterdayTmp = getValue(weatherMap,yesterdayDate,time,WeatherCategoryType.TMP);

        String humidity = getValue(weatherMap, date, time, WeatherCategoryType.REH);
        String yesterdayHumidity = getValue(weatherMap,yesterdayDate,time,WeatherCategoryType.REH);
        String windSpeed = getValue(weatherMap, date, time, WeatherCategoryType.WSD);
        String rainProbability = getValue(weatherMap, date, time, WeatherCategoryType.POP);
        String rainAmount = getValue(weatherMap, date, time, WeatherCategoryType.PCP);
        String skyCondition = getValue(weatherMap, date, time, WeatherCategoryType.SKY);
        String rainStatus = getValue(weatherMap, date, time, WeatherCategoryType.PTY);
        String max = getValue(weatherMap, date, "1500", WeatherCategoryType.TMX);
        String min = getValue(weatherMap, date, "0600", WeatherCategoryType.TMN);

        HumidityResponse humidityDto = getHumidity(humidity, yesterdayHumidity);
        WindSpeedResponse windSpeedDto = getWindSpeed(windSpeed);
        SkyStatus skyStatus = getSkyStatus(skyCondition);
        PrecipitaionResponse precipitationDto = getPrecipitation(rainAmount, rainProbability, rainStatus);
        TemperatureResponse temperatureDto = getTemperature(tmp, yesterdayTmp, min, max);
        return new WeatherResponse(
                null,
                forecastedAt,
                forecastAt,
                location,
                skyStatus,
                precipitationDto,
                humidityDto,
                temperatureDto,
                windSpeedDto
        );

    }

    public WeatherResponse getSingleWeatherResponseDto(
            Map<String,Map<String,String>> weatherMap,
            Map<String, Map<String,String>> yesterdayWeatherMap,
            String date, String time,
            String yesterdayDate,
            Instant forecastedAt,
            Instant forecastAt,
            LocationResponse location
    ) {
        log.info("forecastAt: {}",forecastAt);
        log.info("yesterdayWeatherMap: {}",yesterdayWeatherMap.toString());
        String tmp = getValue(yesterdayWeatherMap, date, time, WeatherCategoryType.TMP);
        String yesterdayTmp = getValue(yesterdayWeatherMap,yesterdayDate,time,WeatherCategoryType.TMP);

        String humidity = getValue(weatherMap, date, time, WeatherCategoryType.REH);
        String yesterdayHumidity = getValue(yesterdayWeatherMap,yesterdayDate,time,WeatherCategoryType.REH);
        String windSpeed = getValue(weatherMap, date, time, WeatherCategoryType.WSD);
        String rainProbability = getValue(weatherMap, date, time, WeatherCategoryType.POP);
        String rainAmount = getValue(weatherMap, date, time, WeatherCategoryType.PCP);
        String skyCondition = getValue(weatherMap, date, time, WeatherCategoryType.SKY);
        String rainStatus = getValue(weatherMap, date, time, WeatherCategoryType.PTY);
        String max = getValue(yesterdayWeatherMap, date, "1500", WeatherCategoryType.TMX);
        String min = getValue(yesterdayWeatherMap, date, "0600", WeatherCategoryType.TMN);

        HumidityResponse humidityDto = getHumidity(humidity, yesterdayHumidity);
        WindSpeedResponse windSpeedDto = getWindSpeed(windSpeed);
        SkyStatus skyStatus = getSkyStatus(skyCondition);
        PrecipitaionResponse precipitationDto = getPrecipitation(rainAmount, rainProbability, rainStatus);
        TemperatureResponse temperatureDto = getTemperature(tmp, yesterdayTmp, min, max);
        return new WeatherResponse(
                null,
                forecastedAt,
                forecastAt,
                location,
                skyStatus,
                precipitationDto,
                humidityDto,
                temperatureDto,
                windSpeedDto
        );

    }

    public WeatherAdministration getWeatherFromApi(String baseDate, String baseTime, int nx, int ny, int numOfRows) {
        return weatherAdministrationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", administrationApiKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .bodyToMono(WeatherAdministration.class)
                .block();
    }


    public List<WeatherAdministrationTime> getWeatherFromFatApi(String baseDate, String baseTime, int nx, int ny, int numOfRows) {
        WeatherAdministration result1 = weatherAdministrationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", administrationApiKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .bodyToMono(WeatherAdministration.class)
                .block();

        WeatherAdministration result2 = weatherAdministrationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", administrationApiKey)
                        .queryParam("pageNo", 2)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .bodyToMono(WeatherAdministration.class)
                .block();

        List<WeatherAdministrationTime> item1 = result1.response().body().items().item();
        List<WeatherAdministrationTime> item2 = result2.response().body().items().item();
        item1.addAll(item2);
        return item1;
    }

    private String getBaseDate(LocalDateTime targetDate) {
        return targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getCurrentTime(Instant time) {
        ZonedDateTime zdt = time.atZone(KST);
        int hour = zdt.getHour();
        return String.format("%02d00", hour + 1);
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

    private String getBaseTime(LocalTime now) {

        if (now.isAfter(LocalTime.of(23, 0))) return "2300";
        if (now.isAfter(LocalTime.of(20, 0))) return "2000";
        if (now.isAfter(LocalTime.of(17, 0))) return "1700";
        if (now.isAfter(LocalTime.of(14, 0))) return "1400";
        if (now.isAfter(LocalTime.of(11, 0))) return "1100";
        if (now.isAfter(LocalTime.of(8, 0))) return "0800";
        if (now.isAfter(LocalTime.of(5, 0))) return "0500";
        if (now.isAfter(LocalTime.of(2, 0))) return "0200";

        return "2300";
    }

    private String getBaseTimeFor3Day(LocalTime now) {

        if (now.isAfter(LocalTime.of(21, 0))) return "2100";
        if (now.isAfter(LocalTime.of(18, 0))) return "1800";
        if (now.isAfter(LocalTime.of(15, 0))) return "1500";
        if (now.isAfter(LocalTime.of(12, 0))) return "1200";
        if (now.isAfter(LocalTime.of(9, 0))) return "0900";
        if (now.isAfter(LocalTime.of(6, 0))) return "0600";
        if (now.isAfter(LocalTime.of(3, 0))) return "0300";
        if (now.isAfter(LocalTime.of(0, 0))) return "0000";

        return "1200";
    }




    private int[] convertToGrid(double latitudeValue, double longitudeValue) {

        double latitude = latitudeValue;
        double longitude = longitudeValue;

        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) /
                Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + latitude * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longitude * DEGRAD - olon;

        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;

        theta *= sn;

        int nx = (int) (ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        return new int[]{nx, ny};
    }

    private TemperatureResponse getTemperature(String temperature,String yesterdayTemperature,String min,String max) {


        return new TemperatureResponse(
                Double.parseDouble(temperature),
                Double.parseDouble(temperature)- Double.parseDouble(yesterdayTemperature),
                Double.parseDouble(min),
                Double.parseDouble(max)
        );


    }

    private WindSpeedResponse getWindSpeed(String windSpeed) {

        Double speed = Double.parseDouble(windSpeed);
        AsWord asWord;
        if (speed > 0 && speed < 4.0) asWord = AsWord.WEAK;
        else if (speed >= 4 && speed < 9.0) asWord = AsWord.MODERATE;
        else if (speed >= 9.0) asWord = AsWord.STRONG;

        else {
            throw new IllegalArgumentException("Invalid speed");
        }
        return new WindSpeedResponse(speed, asWord);
    }

    private SkyStatus getSkyStatus(String skyStatus) {
        int idx = Integer.parseInt(skyStatus);

        switch (idx) {
            case 0, 1, 2, 3, 4, 5 -> {
                return SkyStatus.CLEAR;
            }
            case 6, 8, 7 -> {
                return SkyStatus.CLOUDY;
            }
            case 9, 10 -> {
                return SkyStatus.MOSTLY_CLOUDY;
            }
            default -> throw new IllegalArgumentException("Invalid sky status");

        }


    }




    private HumidityResponse getHumidity(String humidity,String yesterdayHumidity) {

        Double today = Double.parseDouble(humidity);
        Double yesterday = Double.parseDouble(yesterdayHumidity);
        return new HumidityResponse(
                today,
                today - yesterday
        );
    }

    private PrecipitaionResponse getPrecipitation(String rainAmount,String rainProbability, String rainStatus) {

        int ptyIdx = Integer.parseInt(rainStatus);
        PrecipitationType type = switch (ptyIdx) {
            case 0 -> PrecipitationType.NONE;
            case 1 -> PrecipitationType.RAIN;
            case 2 -> PrecipitationType.RAIN_SNOW;
            case 3 -> PrecipitationType.SNOW;
            case 4 -> PrecipitationType.SHOWER;
            default -> throw new IllegalArgumentException("Invalid precipitation");
        };
        Double amount;
        Double probability;
        amount= parsePcp(rainAmount);
        probability = Double.parseDouble(rainProbability);

        return new PrecipitaionResponse(
                type,
                amount,
                probability
        );
    }

    public List<WeatherAdministrationTime> apiTest(WeatherApiTestRequest request) {


        int[] convert = convertToGrid(request.latitude(), request.longitude());
        int nx = convert[0];
        int ny = convert[1];

        WeatherAdministration result = weatherAdministrationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", administrationApiKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", request.numOfRows())
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", request.baseDate())
                        .queryParam("base_time", request.baseTime())
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .bodyToMono(WeatherAdministration.class)
                .block();
        return result.response().body().items().item();

    }

    private double parsePcp(String value) {
        if (value.equals("강수없음")) return 0.0;
        if (value.contains("미만")) return 0.5;
        if (value.contains("~")) {
            String[] split = value.replace("mm", "").split("~");
            return (Double.parseDouble(split[0]) + Double.parseDouble(split[1])) / 2;
        }
        return Double.parseDouble(value.replace("mm", ""));
    }
}
