package com.codeit.weatherfit.domain.auth.controller.docs;

import com.codeit.weatherfit.domain.auth.dto.request.ResetPasswordRequest;
import com.codeit.weatherfit.domain.auth.dto.response.JwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Tag(name = "인증", description = "로그인, 로그아웃, 토큰 갱신, 비밀번호 초기화 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. multipart/form-data로 username, password를 전송합니다.",
            parameters = {
                    @Parameter(name = "username", description = "사용자 이메일", required = true, schema = @Schema(type = "string")),
                    @Parameter(name = "password", description = "비밀번호", required = true, schema = @Schema(type = "string"))
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = JwtDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<JwtDto> signIn(@Parameter(hidden = true) MultipartHttpServletRequest request);

    @Operation(summary = "로그아웃", description = "액세스 토큰과 리프레시 토큰을 무효화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<Void> signOut(
            @Parameter(description = "Bearer 액세스 토큰", example = "Bearer eyJhbGciOiJI...") String authorizationHeader,
            @Parameter(hidden = true) String refreshToken
    );

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰(쿠키)을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "갱신 성공",
                    content = @Content(schema = @Schema(implementation = JwtDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음")
    })
    ResponseEntity<JwtDto> refresh(@Parameter(hidden = true) String refreshToken);

    @Operation(summary = "비밀번호 초기화", description = "이메일로 임시 비밀번호를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "초기화 이메일 발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<Void> resetPassword(ResetPasswordRequest request);

    @Operation(summary = "CSRF 토큰 조회", description = "CSRF 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "토큰 발급 성공")
    })
    ResponseEntity<Void> csrfToken(@Parameter(hidden = true) CsrfToken csrfToken);
}
