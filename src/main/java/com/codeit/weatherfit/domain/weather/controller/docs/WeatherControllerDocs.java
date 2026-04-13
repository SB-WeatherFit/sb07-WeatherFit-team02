package com.codeit.weatherfit.domain.weather.controller.docs;

import com.codeit.weatherfit.domain.weather.dto.request.WeatherApiTestRequest;
import com.codeit.weatherfit.domain.weather.dto.request.WeatherRequest;
import com.codeit.weatherfit.domain.weather.dto.response.KakaoLocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.LocationResponse;
import com.codeit.weatherfit.domain.weather.dto.response.WeatherResponse;
import com.codeit.weatherfit.domain.weather.dto.response.weatherAdministrationApi.WeatherAdministrationTime;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@Tag(name = "날씨", description = "날씨 정보 조회 및 위치 정보 API")
public interface WeatherControllerDocs {

    @Operation(summary = "날씨 정보 조회 (좌표 기반)", description = "위도와 경도를 기반으로 날씨 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = WeatherResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<List<WeatherResponse>> createWeather(
            @ParameterObject WeatherRequest weatherRequest,
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "날씨 정보 조회 (사용자 기본 위치)", description = "로그인한 사용자의 기본 위치 기반 날씨 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = WeatherResponse.class)))
            )
    })
    ResponseEntity<List<WeatherResponse>> getWeather(
            @Parameter(hidden = true) UUID userId
    );

    @Operation(summary = "위치 정보 조회", description = "위도와 경도로 지역명 등 위치 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<LocationResponse> getWeatherLocation(
            @ParameterObject WeatherRequest weatherRequest
    );

    @Hidden
    ResponseEntity<List<WeatherAdministrationTime>> test(WeatherApiTestRequest request);

    @Hidden
    ResponseEntity<KakaoLocationResponse> testKakao(WeatherRequest request);

    @Hidden
    ResponseEntity<List<WeatherResponse>> testUpdateScheduler();

    @Hidden
    String who() throws UnknownHostException;
}
