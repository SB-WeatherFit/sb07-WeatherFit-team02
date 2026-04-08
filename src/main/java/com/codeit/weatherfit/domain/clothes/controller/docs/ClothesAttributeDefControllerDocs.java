package com.codeit.weatherfit.domain.clothes.controller.docs;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "의상 속성 정의", description = "옷 속성 정의 관리 API")
public interface ClothesAttributeDefControllerDocs {

    @Operation(summary = "옷 속성 정의 생성", description = "새로운 옷 속성 정의를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<ClothesAttributeDefDto> create(ClothesAttributeDefCreateRequest request);

    @Operation(summary = "옷 속성 정의 목록 조회", description = "옷 속성 정의 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClothesAttributeDefDto.class)))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<List<ClothesAttributeDefDto>> getAttributeDefs(
            @Parameter(description = "조회 조건") ClothesAttributeDefGetRequest request
    );

    @Operation(summary = "옷 속성 정의 수정", description = "기존 옷 속성 정의를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "정의 없음")
    })
    ResponseEntity<ClothesAttributeDefDto> update(
            @Parameter(description = "속성 정의 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID definitionId,
            ClothesAttributeDefUpdateRequest request
    );

    @Operation(summary = "옷 속성 정의 삭제", description = "옷 속성 정의를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "정의 없음")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "속성 정의 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID definitionId
    );
}