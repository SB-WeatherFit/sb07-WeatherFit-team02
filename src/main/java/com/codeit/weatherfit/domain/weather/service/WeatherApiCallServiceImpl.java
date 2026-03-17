package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.*;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministration;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherCategoryType;
import com.codeit.weatherfit.domain.weather.entity.*;
import com.codeit.weatherfit.domain.weather.exception.WeatherCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public List<WeatherResponse> getWeatherLisFromAdministration(WeatherRequest request, Instant time){
        long before = System.currentTimeMillis();

        LocalDateTime kst = LocalDateTime.ofInstant(time, KST);

        String baseDate = getBaseDate(kst);
        String yesterdayDate = getBaseDate(kst.minusDays(1));
        String tomorrowDate = getBaseDate(kst.plusDays(1));
        String secondDayDate = getBaseDate(kst.plusDays(2));
        String thirdDayDate = getBaseDate(kst.plusDays(3));
        String forthDayDate = getBaseDate(kst.plusDays(4));
        String baseTime = getBaseTime(kst.toLocalTime());
        String currentTime = getCurrentTime(time);


        Instant forecastedAt = getForecastedAt(time);
        LocationResponse tmpLocation = new LocationResponse(
                request.latitude(),
                request.longitude(),
                (int) request.latitude(),
                (int) request.longitude(),
                List.of("서울특별시")
        );


        int nx = convertToGrid(request)[0];
        int ny = convertToGrid(request)[1];

        List<WeatherAdministrationTime> forecastedData = getWeatherFromApi(baseDate, baseTime, nx, ny, 13 * 24 * 5)
                .response()
                .body()
                .items()
                .item();
        List<WeatherAdministrationTime> yesterdayForecastedData = getWeatherFromApi(yesterdayDate, "0500", nx, ny, 13 * 24 * 2)
                .response()
                .body()
                .items()
                .item();

        List<WeatherAdministrationTime> todayItem = forecastedData.stream()
                .filter(item -> item.fcstDate().equals(baseDate) && item.fcstTime().equals(currentTime))
                .toList();
        List<WeatherAdministrationTime> tomorrowItem = forecastedData.stream()
                .filter(item -> item.fcstDate().equals(tomorrowDate) && item.fcstTime().equals(currentTime))
                .toList();
        List<WeatherAdministrationTime> secondDayItem = forecastedData.stream()
                .filter(item -> item.fcstDate().equals(secondDayDate) && item.fcstTime().equals(currentTime))
                .toList();
        List<WeatherAdministrationTime> thirdDayItem = forecastedData.stream()
                .filter(item -> item.fcstDate().equals(thirdDayDate) && item.fcstTime().equals(currentTime))
                .toList();

        List<WeatherAdministrationTime> forthDayItem = forecastedData.stream()
                .filter(item -> item.fcstDate().equals(forthDayDate) && item.fcstTime().equals(currentTime))
                .toList();
        WeatherResponse todayResponse = getSingleWeatherResponseDto(
                todayItem,
                getMin(yesterdayForecastedData, baseDate),
                getMax(yesterdayForecastedData, baseDate),
                getTargetHumidity(forecastedData, yesterdayDate, currentTime),
                getTargetTemperature(forecastedData, yesterdayDate, currentTime),
                forecastedAt,
                forecastedAt.plus(1, ChronoUnit.MINUTES),
                tmpLocation
        );

        WeatherResponse tomorrowResponse = getSingleWeatherResponseDto(
                tomorrowItem,
                getMin(forecastedData, tomorrowDate),
                getMax(forecastedData, tomorrowDate),
                getTargetHumidity(forecastedData, baseDate, currentTime),
                getTargetTemperature(forecastedData, baseDate, currentTime),
                forecastedAt,
                forecastedAt.plus(1, ChronoUnit.DAYS),
                tmpLocation

        );

        WeatherResponse secondResponse = getSingleWeatherResponseDto(
                secondDayItem,
                getMin(forecastedData, secondDayDate),
                getMax(forecastedData, secondDayDate),
                getTargetHumidity(forecastedData, tomorrowDate, currentTime),
                getTargetTemperature(forecastedData, tomorrowDate, currentTime),
                forecastedAt,
                forecastedAt.plus(2, ChronoUnit.DAYS),
                tmpLocation

        );

        WeatherResponse thirdResponse = getSingleWeatherResponseDto(
                thirdDayItem,
                getMin(forecastedData, thirdDayDate),
                getMax(forecastedData, thirdDayDate),
                getTargetHumidity(forecastedData, secondDayDate, currentTime),
                getTargetTemperature(forecastedData, secondDayDate, currentTime),
                forecastedAt,
                forecastedAt.plus(3, ChronoUnit.DAYS),
                tmpLocation

        );

        WeatherResponse forthResponse = getSingleWeatherResponseDto(
                forecastedData,
                getMin(forecastedData, forthDayDate),
                getMax(forecastedData, forthDayDate),
                getTargetHumidity(forecastedData, thirdDayDate, currentTime),
                getTargetTemperature(forecastedData, thirdDayDate, currentTime),
                forecastedAt,
                forecastedAt.plus(4, ChronoUnit.DAYS),
                tmpLocation

        );

        long after = System.currentTimeMillis();
        log.info("spending time to call list:{} seconds",(after-before)/1000.0);

        return List.of(todayResponse,tomorrowResponse,secondResponse,thirdResponse,forthResponse);
    }


    public WeatherResponse getSingleWeatherResponseDto(List<WeatherAdministrationTime> targetItem,
                                                       String min,
                                                       String max,
                                                       Double targetYesterdayHumidity,
                                                       Double targetYesterdayTemperature,
                                                       Instant forecastedAt,
                                                       Instant forecastAt,
                                                       LocationResponse location
                                                       ){
        Temperature temperature = getTemperature(targetItem, targetYesterdayTemperature, min, max);
        TemperatureResponse temperatureDto = TemperatureResponse.from(temperature);
        WindSpeed windSpeed = getWindSpeed(targetItem);
        WindSpeedResponse windSpeedDto = WindSpeedResponse.from(windSpeed);
        Humidity humidity = getHumidity(targetItem, targetYesterdayHumidity);
        HumidityResponse humidityDto = HumidityResponse.from(humidity);
        SkyStatus skyStatus = getSkyStatus(targetItem);
        Precipitation precipitation = getPrecipitation(targetItem);
        PrecipitaionResponse precipitationDto = PrecipitaionResponse.from(precipitation);

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
//    public WeatherResponse getWeatherFromAdministration(WeatherRequest request, Instant time) {
//
//        long before = System.currentTimeMillis();
//        LocalDateTime kst = LocalDateTime.ofInstant(time, KST);
//
//        String baseDate = getBaseDate(kst);
//        String yesterdayDate = getBaseDate(kst.minusDays(1));
//
//
//        String baseTime = getBaseTime(kst.toLocalTime());
//        String currentTime = getCurrentTime(time);
//        int nx = convertToGrid(request)[0];
//        int ny = convertToGrid(request)[1];
//        log.info("currentTime: {}",currentTime);
//
//        Location location = Location.create(request.latitude(), request.longitude(), 0, 0, List.of("서울특별"));
//        WeatherAdministration totalData = getWeatherFromApi(baseDate, baseTime, nx, ny, 13 * 24);
//        WeatherAdministration yesterdayData = getWeatherFromApi(yesterdayDate, "0500", nx, ny, 13 * 24 * 2);
//
//        List<WeatherAdministrationTime> totalItem=totalData
//                .response()
//                .body()
//                .items()
//                .item();
//        List<WeatherAdministrationTime> todayItems = totalData
//                .response()
//                .body()
//                .items()
//                .item()
//                .stream()
//                .filter(x -> x.fcstDate().equals(baseDate) && x.fcstTime().equals(currentTime))
//                .toList();
//        List<WeatherAdministrationTime> yesterdayItems = yesterdayData.response()
//                .body()
//                .items()
//                .item();
//
//        String tmx = yesterdayItems.stream()
//                .filter(item -> item.category().equals(WeatherCategoryType.TMX.name()))
//                .filter(item -> item.fcstDate().equals(baseDate))
//                .map(WeatherAdministrationTime::fcstValue)
//                .findFirst()
//                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.TMX));
//
//        String tmn = yesterdayItems.stream()
//                .filter(item -> item.category().equals(WeatherCategoryType.TMN.name()))
//                .filter(item -> item.fcstDate().equals(baseDate)) // baseDate 이후 날짜
//                .map(WeatherAdministrationTime::fcstValue)
//                .findFirst()
//                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.TMN));
//
//
//        Instant forecastedAt = getForecastedAt(time);
//        Instant forecastAt = forecastedAt.plus(Duration.ofHours(1));
//
//        Temperature temperature = getTemperature(todayItems, yesterdayItems, yesterdayDate, currentTime, tmn, tmx);
//        WindSpeed windSpeed = getWindSpeed(todayItems, currentTime);
//        Precipitation precipitation = getPrecipitation(todayItems, currentTime);
//        SkyStatus skyStatus = getSkyStatus(todayItems, currentTime);
//        Humidity humidity = getHumidity(todayItems, yesterdayItems, yesterdayDate, currentTime);
//
//        Weather weather = Weather.create(
//                temperature,
//                windSpeed,
//                precipitation,
//                skyStatus,
//                humidity,
//                forecastedAt,
//                forecastAt,
//                location
//        );
//        long after = System.currentTimeMillis();
//        log.info("Spending time: {}",(after-before)/1000.0);
//        return WeatherResponse.from(weather);
//    }

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

    private String getBaseDate(LocalDateTime targetDate) {
        return targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getCurrentTime(Instant time) {
        ZonedDateTime zdt = time.atZone(KST);
        int hour = zdt.getHour();
        return String.format("%02d00", hour+1);
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
    private Double getTargetHumidity(List<WeatherAdministrationTime> forecastedData, String targetDate, String targetTime) {
        String humidity = forecastedData.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.REH))
                .filter(x -> x.fcstDate().equals(targetDate) && x.fcstTime().equals(targetTime))
                .map(x -> x.fcstValue())
                .findFirst()
                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.REH));
        return Double.valueOf(humidity);

    }

    private Double getTargetTemperature(List<WeatherAdministrationTime> forecastedData, String targetDate,String targetTime) {
        String temperature = forecastedData.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.TMP))
                .filter(x -> x.fcstDate().equals(targetDate) && x.fcstTime().equals(targetTime))
                .map(x -> x.fcstValue())
                .findFirst()
                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.TMP));
        return Double.valueOf(temperature);

    }
    private int[] convertToGrid(WeatherRequest request) {

        double latitude = request.latitude();
        double longitude = request.longitude();

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

    private Temperature getTemperature(List<WeatherAdministrationTime> todayItem,
                                       Double yesterdayTemperature,
                                       String min,
                                       String max) {


        List<WeatherAdministrationTime> tempLis = todayItem.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.TMP.name()))
                .toList();

        Double temperature = tempLis.stream()
                .map(item -> Double.parseDouble(item.fcstValue()))
                .findFirst()
                .orElseThrow(() ->new WeatherCategoryNotFoundException(WeatherCategoryType.TMP));

        return new Temperature(
                temperature,
                temperature - yesterdayTemperature,
                Double.parseDouble(min),
                Double.parseDouble(max)
        );


    }

    private WindSpeed getWindSpeed(List<WeatherAdministrationTime> itemLis) {
        List<WeatherAdministrationTime> windLis = itemLis.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.SKY.name()))
                .toList();
        String windSpeed = windLis.stream()
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(() ->new WeatherCategoryNotFoundException(WeatherCategoryType.SKY));
        Double speed = Double.parseDouble(windSpeed);
        AsWord asWord;
        if (speed > 0 && speed < 4.0) asWord = AsWord.WEAK;
        else if (speed >= 4 && speed < 9.0) asWord = AsWord.MODERATE;
        else if (speed >= 9.0) asWord = AsWord.STRONG;

        else {
            log.info("speed: {}", speed);
            throw new IllegalArgumentException("Invalid speed");
        }
        return new WindSpeed(asWord, speed);
    }

    private SkyStatus getSkyStatus(List<WeatherAdministrationTime> itemLis) {
        String skyStatus = itemLis.stream()
                .filter(
                        x -> x.category().equals(WeatherCategoryType.SKY.name())
                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(()->new WeatherCategoryNotFoundException(WeatherCategoryType.SKY));
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
    private String getMax(List<WeatherAdministrationTime> forecastedData, String targetDate){
        String max = forecastedData.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.TMX.name()))
                .filter(x -> x.fcstDate().equals(targetDate))
                .map(x -> x.fcstValue())
                .findFirst()
                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.TMX));
        return max;

    }

    private String getMin(List<WeatherAdministrationTime> forecastedData, String targetDate){
        String max = forecastedData.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.TMN.name()))
                .filter(x -> x.fcstDate().equals(targetDate))
                .map(x -> x.fcstValue())
                .findFirst()
                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.TMN));
        return max;

    }


    private Humidity getHumidity(List<WeatherAdministrationTime> todayItemLis, Double yesterdayHumidity) {
        String humidityData = todayItemLis.stream().filter(
                        x -> x.category().equals(WeatherCategoryType.REH.name())
                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(() -> new WeatherCategoryNotFoundException(WeatherCategoryType.REH));
        Double todayHumidity = Double.parseDouble(humidityData);

        return new Humidity(
                todayHumidity,
                todayHumidity - yesterdayHumidity
        );
    }

    private Precipitation getPrecipitation(List<WeatherAdministrationTime> itemLis) {
        String ptyValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.PTY.name())
                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(()->new WeatherCategoryNotFoundException(WeatherCategoryType.PTY));


        String popValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.POP.name())

                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(()->new WeatherCategoryNotFoundException(WeatherCategoryType.POP));

        String pcpValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.PCP.name())
                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(()-> new WeatherCategoryNotFoundException(WeatherCategoryType.PCP));

        int ptyIdx = Integer.parseInt(ptyValue);
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

        if (pcpValue.equals(NO_RAIN_AMOUNT)) amount = 0.0;
        else amount = Double.parseDouble(pcpValue);
        probability = Double.parseDouble(popValue);

        return new Precipitation(
                type,
                amount,
                probability
        );
    }

}
