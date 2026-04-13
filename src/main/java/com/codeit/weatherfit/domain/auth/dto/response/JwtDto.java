package com.codeit.weatherfit.domain.auth.dto.response;

import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record JwtDto(
        @Schema(description = "사용자 정보") UserDto userDto,
        @Schema(description = "액세스 토큰") String accessToken
) {
    public static JwtDto of(UserDto userDto, String accessToken) {
        return new JwtDto(userDto, accessToken);
    }
}