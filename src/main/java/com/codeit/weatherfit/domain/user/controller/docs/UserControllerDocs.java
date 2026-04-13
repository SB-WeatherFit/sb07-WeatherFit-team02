package com.codeit.weatherfit.domain.user.controller.docs;

import com.codeit.weatherfit.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "사용자", description = "사용자 생성, 조회, 역할/잠금/비밀번호 수정 API")
public interface UserControllerDocs {

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    UserDto create(UserCreateRequest request);

    @Operation(summary = "사용자 목록 조회", description = "커서 기반 페이지네이션으로 사용자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDtoCursorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    UserDtoCursorResponse getUsers(
            @Parameter(description = "커서 값") String cursor,
            @Parameter(description = "커서 이후 ID") UUID idAfter,
            @Parameter(description = "조회 개수", required = true) int limit,
            @Parameter(description = "정렬 기준", required = true, schema = @Schema(allowableValues = {"createdAt"})) String sortBy,
            @Parameter(description = "정렬 방향", required = true, schema = @Schema(allowableValues = {"ASCENDING", "DESCENDING"})) String sortDirection,
            @Parameter(description = "이메일 검색") String emailLike,
            @Parameter(description = "역할 필터", schema = @Schema(allowableValues = {"USER", "ADMIN"})) String roleEqual,
            @Parameter(description = "잠금 상태 필터") Boolean locked
    );

    @Operation(summary = "사용자 역할 수정", description = "사용자의 역할을 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    UserDto updateRole(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            UserRoleUpdateRequest request
    );

    @Operation(summary = "사용자 잠금 상태 수정", description = "사용자의 잠금 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    UserDto updateLock(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            UserLockUpdateRequest request
    );

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    ResponseEntity<Void> updatePassword(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            ChangePasswordRequest request
    );
}
