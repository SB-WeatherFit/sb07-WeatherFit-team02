package com.codeit.weatherfit.domain.weather.service;

import com.codeit.weatherfit.domain.profile.entity.Location;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherApi.WeatherApiResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministration;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherCategoryType;
import com.codeit.weatherfit.domain.weather.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApiCallServiceImpl implements WeatherApiCallService {


    private final WebClient weatherApiWebClient;
    private final WebClient weatherAdministrationClient;
    private static final String NO_RAIN_AMOUNT = "강수없음";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Value("${open-weather-map.api-key}")
    private String apiKey;

    @Value("${open-administration-map.api-key}")
    private String administrationApiKey;

    private final String apiLanguage = "kr";
    @Override
    public WeatherApiResponse getWeathersFromNow(WeatherRequest weatherRequest) {
        double latitude = weatherRequest.latitude();
        double longitude = weatherRequest.longitude();

        long before = System.currentTimeMillis();
        WeatherApiResponse result = weatherApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", apiKey)
                        .queryParam("lang", apiLanguage)
                        .queryParam("units", "metric")
                        .build()
                )
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .block();
        long after = System.currentTimeMillis();

        log.info("spending time: {} sec", (after - before) / 1000.0);
        return result;
    }

    public WeatherResponse getWeatherFromAdministration(WeatherRequest request,Instant time) throws InterruptedException {

        LocalDateTime kst = LocalDateTime.ofInstant(time, KST);

        String baseDate = getBaseDate(kst);
        String yesterdayDate = getBaseDate(kst.minusDays(1));

        log.info("baseDate: {}, yesterdayDate: {}", baseDate, yesterdayDate);
        String currentTime = getCurrentTime(kst.toLocalTime());
        int nx = convertToGrid(request)[0];
        int ny = convertToGrid(request)[1];

        Location location = Location.create(request.latitude(), request.longitude(), 0, 0, List.of("서울특별"));
        WeatherAdministration result = getWeatherFromApi(baseDate, currentTime, nx, ny, 13 * 24 * 2);
        Thread.sleep(1L);

        WeatherAdministration tmxData = getWeatherFromApi(yesterdayDate, "0500", nx, ny, 13 * 12 * 5);
        List<WeatherAdministrationTime> atTmxTime = tmxData.response()
                .body()
                .items()
                .item();
        Thread.sleep(1L);

        String tmx = atTmxTime.stream()
                .filter(item -> item.category().equals(WeatherCategoryType.TMX.name()))
                .filter(item -> item.fcstDate().equals(baseDate))
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TMX"));

        String tmn = atTmxTime.stream()
                .filter(item -> item.category().equals(WeatherCategoryType.TMN.name()))
                .filter(item -> item.fcstDate().equals(baseDate)) // baseDate 이후 날짜
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()

                .orElseThrow(() -> new IllegalArgumentException("No TMN"));
        List<WeatherAdministrationTime> items = result
                .response()
                .body()
                .items()
                .item();

        Instant forecastedAt = getForecastedAt(time);
        Instant forecastAt = forecastedAt.plus(Duration.ofHours(1));

        Temperature temperature = getTemperature(items, currentTime,tmn,tmx);
        WindSpeed windSpeed = getWindSpeed(items, currentTime);
        Precipitation precipitation = getPrecipitation(items, currentTime);
        SkyStatus skyStatus = getSkyStatus(items, currentTime);
        Humidity humidity = getHumidity(items, currentTime);

        Weather weather = Weather.create(
                temperature,
                windSpeed,
                precipitation,
                skyStatus,
                humidity,
                forecastedAt,
                forecastAt,
                location
        );

        return WeatherResponse.from(weather);
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

    private WeatherAdministrationTime getTmxData(String yesterdayDate, int nx, int ny) {
        WeatherAdministration response = getWeatherFromApi(yesterdayDate, "0500", nx, ny, 100);
        return response.response().body().items().item().stream()
                .filter(item -> item.category().equals("TMX") && item.fcstDate().equals(yesterdayDate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TMX data"));
    }

    private WeatherAdministrationTime getTmnData(String yesterdayDate, int nx, int ny) {

        WeatherAdministration response = getWeatherFromApi(yesterdayDate, "2000", nx, ny, 100);
        return response.response().body().items().item().stream()
                .filter(item -> item.category().equals("TMN") && item.fcstDate().equals(yesterdayDate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TMN data"));
    }

    private String getBaseDate(LocalDateTime targetDate) {
        return targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private Instant getForecastedAt(Instant targetTime){
        List<Integer> timeLis = List.of(2, 5, 8, 11, 14, 17, 20, 23);
        ZonedDateTime zonedDateTime = targetTime.atZone(KST);

        int targetHour = zonedDateTime.getHour();
        int baseHour = timeLis.stream()
                .filter(hour -> hour <= targetHour)
                .max(Integer::compareTo)
                .orElse(23);


        ZonedDateTime forecastedAt = zonedDateTime.withHour(baseHour).withMinute(0).withSecond(0).withNano(0);
        if (targetHour<2 ) forecastedAt = forecastedAt.minusDays(1);
        return forecastedAt.toInstant();
    }
    private String getCurrentTime(LocalTime now) {

        if (now.isAfter(LocalTime.of(23,0))) return "2300";
        if (now.isAfter(LocalTime.of(20,0))) return "2000";
        if (now.isAfter(LocalTime.of(17,0))) return "1700";
        if (now.isAfter(LocalTime.of(14,0))) return "1400";
        if (now.isAfter(LocalTime.of(11,0))) return "1100";
        if (now.isAfter(LocalTime.of(8,0))) return "0800";
        if (now.isAfter(LocalTime.of(5,0))) return "0500";
        if (now.isAfter(LocalTime.of(2,0))) return "0200";

        return "2300";
    }
    private int [] convertToGrid(WeatherRequest request){

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

        int nx = (int)(ra * Math.sin(theta) + XO + 0.5);
        int ny = (int)(ro - ra * Math.cos(theta) + YO + 0.5);

        return new int[]{nx, ny};
    }

    private Temperature getTemperature(List<WeatherAdministrationTime> itemLis, String currentTime,String min,String max){


        List<WeatherAdministrationTime> tempLis = itemLis.stream()
                .filter(x -> x.category().equals(WeatherCategoryType.TMP.name())).toList();

        Double temperature = tempLis.stream().filter(x -> {
            log.info("base time :{}",x.baseTime());
            return x.baseTime().equals(currentTime);
        }
                )
                .map(item -> Double.parseDouble(item.fcstValue()))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException( "Invalid Filter3"))
                ;

        return new Temperature(
                temperature,
                0,
                Double.parseDouble(min),
                Double.parseDouble(max)
        );



    }

    private WindSpeed getWindSpeed(List<WeatherAdministrationTime> itemLis, String currentTime){
        List<WeatherAdministrationTime> windLis = itemLis.stream().filter(x -> x.category().equals(WeatherCategoryType.SKY.name()))
                .toList();
        String windSpeed = windLis.stream().filter(
                        x -> x.baseTime().equals(currentTime)
                ).map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException( "Invalid Filter4"));
        Double speed= Double.parseDouble(windSpeed);
        AsWord asWord;
        if(speed >0 && speed<4.0) asWord = AsWord.WEAK;
        else if(speed>=4 && speed<9.0) asWord = AsWord.MODERATE;
        else if (speed>=9.0) asWord= AsWord.STRONG;

        else{
            log.info("speed: {}",speed);
            throw new IllegalArgumentException("Invalid speed");
        }
        return new WindSpeed(asWord,speed);
    }

    private SkyStatus getSkyStatus( List<WeatherAdministrationTime> itemLis, String currentTime){
        String skyStatus = itemLis.stream().filter(
                x -> x.category().equals(WeatherCategoryType.SKY.name())
                            && x.baseTime().equals(currentTime)
        )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow();
        int idx = Integer.parseInt(skyStatus);

        switch(idx){
            case 0,1,2,3,4,5 -> {
                return SkyStatus.CLEAR;
            }
            case 6,8,7 ->{
                return SkyStatus.CLOUDY;
            }
            case 9,10 ->{
                return  SkyStatus.MOSTLY_CLOUDY;
            }
            default -> throw new IllegalArgumentException("Invalid sky status");

        }


    }

    private Humidity getHumidity(List<WeatherAdministrationTime> itemLis, String currentTime){
        String humidityData = itemLis.stream().filter(
                        x -> x.category().equals(WeatherCategoryType.REH.name())
                                && x.baseTime().equals(currentTime)
                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow();

        return new Humidity(
                Double.parseDouble(humidityData),
                0
        );
    }

    private Precipitation getPrecipitation(List<WeatherAdministrationTime> itemLis, String currentTime){
        String ptyValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.PTY.name())
                                && x.baseTime().equals(currentTime)

                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow();


        String popValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.POP.name())
                                && x.baseTime().equals(currentTime)

                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow();

        String pcpValue = itemLis.stream().filter(x ->
                        x.category().equals(WeatherCategoryType.PCP.name())
                                && x.baseTime().equals(currentTime)

                )
                .map(WeatherAdministrationTime::fcstValue)
                .findFirst()
                .orElseThrow();

        int ptyIdx = Integer.parseInt(ptyValue);
        PrecipitationType type = switch (ptyIdx){
            case 0 -> PrecipitationType.NONE;
            case 1 -> PrecipitationType.RAIN;
            case 2 -> PrecipitationType.RAIN_SNOW;
            case 3 -> PrecipitationType.SNOW;
            case 4 -> PrecipitationType.SHOWER;
            default -> throw new IllegalArgumentException("Invalid precipitation");
        };
        Double amount;
        Double probability;

        if(pcpValue.equals(NO_RAIN_AMOUNT)) amount = 0.0;
        else amount = Double.parseDouble(pcpValue);
        probability = Double.parseDouble(popValue);

        return new Precipitation(
                type,
                amount,
                probability
        );
    }

    public List<WeatherAdministrationTime> realTest(WeatherRequest request){

        LocalDateTime kst = LocalDateTime.ofInstant(Instant.now(), KST);
        log.info("hello");

        String baseDate = getBaseDate(kst.minusDays(1));
        String currentTime = getCurrentTime(kst.toLocalTime());
        int nx = convertToGrid(request)[0];
        int ny = convertToGrid(request)[1];
        long before = System.currentTimeMillis();
        WeatherAdministration result = weatherAdministrationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", administrationApiKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 12*12*5*10) // 오전 2시 부터 오후 11시까지 총 8번 발표
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time","0800")
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .bodyToMono(WeatherAdministration.class)
                .block();

        List<WeatherAdministrationTime> items = result
                .response()
                .body()
                .items()
                .item();
        long after = System.currentTimeMillis();
        log.info("[기상청 예보 api 호출 시간] :{}",(after-before)/1000.0);
        return items;
    }
}
