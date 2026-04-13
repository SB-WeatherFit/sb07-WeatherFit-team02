package com.codeit.weatherfit.domain.feed.controller.docs;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.feed.dto.CommentDto;
import com.codeit.weatherfit.domain.feed.dto.FeedDto;
import com.codeit.weatherfit.domain.feed.dto.request.CommentCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.CommentGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedGetRequest;
import com.codeit.weatherfit.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherfit.domain.feed.dto.response.CommentGetResponse;
import com.codeit.weatherfit.domain.feed.dto.response.FeedGetResponse;
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

@Tag(name = "피드", description = "피드 CRUD, 댓글, 좋아요 API")
public interface FeedControllerDocs {

    @Operation(summary = "피드 목록 조회", description = "커서 기반 페이지네이션으로 피드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FeedGetResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<FeedGetResponse> get(
            @ParameterObject FeedGetRequest request,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "피드 생성", description = "새로운 피드를 작성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = FeedDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<FeedDto> createFeed(
            FeedCreateRequest request,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "댓글 목록 조회", description = "특정 피드의 댓글을 커서 기반 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentGetResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<CommentGetResponse> getComment(
            @ParameterObject CommentGetRequest request,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "댓글 작성", description = "특정 피드에 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "작성 성공",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "피드 없음")
    })
    ResponseEntity<CommentDto> createComment(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            CommentCreateRequest request,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "댓글 삭제", description = "특정 피드의 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Parameter(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID commentId,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "좋아요", description = "피드에 좋아요를 누릅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "좋아요 성공"),
            @ApiResponse(responseCode = "404", description = "피드 없음")
    })
    ResponseEntity<Void> like(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "좋아요 취소", description = "피드의 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "취소 성공"),
            @ApiResponse(responseCode = "404", description = "피드 없음")
    })
    ResponseEntity<Void> unlike(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "피드 수정", description = "피드 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = FeedDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "피드 없음")
    })
    ResponseEntity<FeedDto> update(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            FeedUpdateRequest request,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );

    @Operation(summary = "피드 삭제", description = "피드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "피드 없음")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "피드 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Parameter(hidden = true) WeatherFitUserDetails userDetails
    );
}
