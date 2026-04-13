package com.codeit.weatherfit.domain.profile.controller.docs;

import com.codeit.weatherfit.domain.profile.dto.request.ProfileUpdateRequest;
import com.codeit.weatherfit.domain.profile.dto.response.ProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "프로필", description = "사용자 프로필 조회 및 수정 API")
public interface ProfileControllerDocs {

    @Operation(summary = "프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    ProfileDto get(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId
    );

    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보와 프로필 이미지를 multipart/form-data로 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    ProfileDto update(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            ProfileUpdateRequest request,
            @Parameter(description = "프로필 이미지 파일") MultipartFile image
    );
}
