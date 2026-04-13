package com.codeit.weatherfit.domain.follow.controller.docs;

import com.codeit.weatherfit.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherfit.domain.follow.dto.request.FollowerSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.request.FolloweeSearchCondition;
import com.codeit.weatherfit.domain.follow.dto.response.FollowDto;
import com.codeit.weatherfit.domain.follow.dto.response.FollowListResponse;
import com.codeit.weatherfit.domain.follow.dto.response.FollowSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "팔로우", description = "팔로우, 언팔로우, 팔로우 목록 조회 API")
public interface FollowControllerDocs {

    @Operation(summary = "팔로우", description = "다른 사용자를 팔로우합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "팔로우 성공",
                    content = @Content(schema = @Schema(implementation = FollowDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<FollowDto> createFollow(FollowCreateRequest createRequest);

    @Operation(summary = "팔로우 요약 조회", description = "특정 사용자의 팔로워/팔로잉 수와 팔로우 관계를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FollowSummaryDto.class))
            )
    })
    ResponseEntity<FollowSummaryDto> getFollowSummary(
            @Parameter(description = "조회 대상 사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            @Parameter(hidden = true) UUID myId
    );

    @Operation(summary = "팔로잉 목록 조회", description = "특정 사용자가 팔로우하는 사용자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FollowListResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<FollowListResponse> getFollowings(
            @ParameterObject FolloweeSearchCondition condition
    );

    @Operation(summary = "팔로워 목록 조회", description = "특정 사용자를 팔로우하는 사용자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FollowListResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<FollowListResponse> getFollowers(
            @ParameterObject FollowerSearchCondition condition
    );

    @Operation(summary = "언팔로우", description = "팔로우를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "404", description = "팔로우 관계 없음")
    })
    ResponseEntity<Void> unfollow(
            @Parameter(description = "팔로우 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID followId
    );
}
